package com.caiwillie.mubu2anki.converter;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import be.ceau.opml.entity.Outline;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.mubu2anki.model.MubuOutline;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.caiwillie.mubu2anki.common.Constant.MUBU_TEXT;

/**
 * @author caiwillie
 */
public class MubuConverter {

    private static final Pattern SN_PATTERN = Pattern.compile("^(\\d{1,}(\\.\\d{1,}){0,} |（([\\u2E80-\\u9FFF]|\\w){1,}）)");

    public static MubuOutline convert(String path) {

        Opml opml = null;
        try (InputStream in = FileUtil.getInputStream(path)) {
            opml = new OpmlParser().parse(in);
        } catch (IOException exception) {
            throw new IllegalArgumentException(StrUtil.format("文件 {} 读取失败", path));
        } catch (OpmlParseException exception) {
            throw new IllegalArgumentException(StrUtil.format("文件 {} 不是opml格式", path));
        }

        String title = StrUtil.trim(opml.getHead().getTitle());

        Assert.notNull(title, "文件 {} 文档标题不能为空", path);


        MubuOutline root = new MubuOutline();
        root.setSn(null);
        root.setText(title);

        List<MubuOutline> mubuOutlines = convert(opml.getBody().getOutlines());

        return null;
    }

    private static List<MubuOutline> convert(List<Outline> outlines) {
        List<MubuOutline> ret = new ArrayList<>();

        if(CollUtil.isEmpty(outlines)) {
            return ret;
        }

        for (Outline outline : outlines) {
            ret.add(convert(outline));
        }

        return ret;
    }

    private static MubuOutline convert(Outline outline) {
        String mubuText = outline.getAttribute(MUBU_TEXT);
        String html = URLDecoder.decode(mubuText, StandardCharsets.UTF_8);
        // 将html格式的text内容解析成doc
        Document doc = Jsoup.parse(html);
        Elements spans = doc.getElementsByTag("span");

        String span0 = StrUtil.trim(spans.get(0).text());
        String group0 = ReUtil.getGroup0(SN_PATTERN, span0);

        MubuOutline ret = new MubuOutline();
        ret.setSn(group0);
        ret.setText(span0);

        List<MubuOutline> children = convert(outline.getSubElements());
        ret.setChildern(children);
        return ret;
    }

    private static void removeSN(List<MubuOutline> mubuOutlines) {
        if(CollUtil.isEmpty(mubuOutlines)) {
            return;
        }


    }
}
