package com.caiwillie.mubu2anki.converter;

import be.ceau.opml.entity.Opml;
import be.ceau.opml.entity.Outline;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.mubu2anki.model.MubuOutline;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.caiwillie.mubu2anki.common.Constant.MUBU_TEXT;

public class MubuConverter {

    private static final Pattern SN_PATTERN = Pattern.compile("^[1-9]\\d{0,}(\\.[1-9]\\d{0,}){0,}$");


    public MubuOutline convert(Opml opml, boolean hasSN) {
        MubuOutline ret = null;
        if (opml == null) {
            return ret;
        }
        String title = opml.getHead().getTitle();

        Assert.notNull(title, "标题不能为空");

        ret = new MubuOutline();

        ret.setText(title);

        // 首个序号的依据就是标题
        ret.setSn(title);

        List<MubuOutline> children = new ArrayList<>();

        ret.setChildern(children);

        List<Outline> outlines = opml.getBody().getOutlines();

        if(CollUtil.isNotEmpty(outlines)) {
            for (Outline outline : outlines) {
                children.add(convert(outline, hasSN));
            }
        }

        return ret;
    }

    private MubuOutline convert(Outline o, boolean hasSN) {
        MubuOutline ret = null;
        if(o == null) {
            return null;
        }

        ret = new MubuOutline();

        String[] strArr = convert(o.getAttribute(MUBU_TEXT), hasSN);

        ret.setSn(strArr[0]);

        ret.setText(strArr[1]);

        List<MubuOutline> children = new ArrayList<>();

        ret.setChildern(children);

        List<Outline> outlines = o.getSubElements();

        if(CollUtil.isNotEmpty(outlines)) {
            for (Outline outline : outlines) {
                children.add(convert(outline, hasSN));
            }
        }

        return ret;
    }

    String[] convert (String text, boolean hasSN) {
        String[] ret = new String[2];
        if(text == null) {
            return ret;
        }

        String html = URLDecoder.decode(text, StandardCharsets.UTF_8);
        // 将html格式的text内容解析成doc
        Document doc = Jsoup.parse(html);
        Elements spans = doc.getElementsByTag("span");
        if(CollUtil.isEmpty(spans)) {
            return ret;
        }

        String span0 = spans.get(0).text();
        if(!hasSN) {
            ret[0] = span0;
            ret[1] = span0;
            return ret;
        }

        int index1 = span0.indexOf(" ");
        int index2 = span0.indexOf("：");
        Assert.isTrue(index1 > 0 || index2 > 0, "内容 {} 未找到序号", span0);
        String sn = null;
        String content = null;
        if(index1 > 0) {
            sn = StrUtil.trimToEmpty(span0.substring(0, index1));
            boolean match = ReUtil.isMatch(SN_PATTERN, sn);
            Assert.isTrue(match, "内容 {} 未匹配到序号", span0);
            content = span0.substring(index1 + 1);
        } else {
            sn = StrUtil.trimToEmpty(span0.substring(0, index2));
            content = span0.substring(index2 + 1);
        }
        
        ret[0] = sn;
        ret[1] = content;

        return ret;
    }

}
