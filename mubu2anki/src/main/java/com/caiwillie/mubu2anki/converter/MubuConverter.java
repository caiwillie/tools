package com.caiwillie.mubu2anki.converter;

import be.ceau.opml.entity.Opml;
import be.ceau.opml.entity.Outline;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLDecoder;
import com.caiwillie.mubu2anki.common.Constant;
import com.caiwillie.mubu2anki.model.MubuOutline;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.caiwillie.mubu2anki.common.Constant.MUBU_TEXT;

public class MubuConverter {


    public MubuOutline convert(Opml opml) {
        MubuOutline ret = null;
        if (opml == null) {
            return ret;
        }
        String title = opml.getHead().getTitle();

        Assert.notNull(title, "标题不能为空");

        ret = new MubuOutline();

        ret.setText(title);

        List<MubuOutline> children = new ArrayList<>();

        ret.setChildern(children);

        List<Outline> outlines = opml.getBody().getOutlines();

        if(CollUtil.isNotEmpty(outlines)) {
            for (Outline outline : outlines) {
                children.add(convert(outline));
            }
        }

        return ret;
    }

    private MubuOutline convert(Outline o) {
        MubuOutline ret = null;
        if(o == null) {
            return null;
        }

        ret = new MubuOutline();

        ret.setText(convert(o.getAttribute(MUBU_TEXT)));

        List<MubuOutline> children = new ArrayList<>();

        ret.setChildern(children);

        List<Outline> outlines = o.getSubElements();

        if(CollUtil.isNotEmpty(outlines)) {
            for (Outline outline : outlines) {
                children.add(convert(outline));
            }
        }

        return ret;
    }

    String convert (String text) {
        String ret = null;
        if(text == null) {
            return ret;
        }

        String html = URLDecoder.decode(text, Charset.defaultCharset());
        Document doc = Jsoup.parse(html);
        Elements spans = doc.getElementsByTag("span");
        if(CollUtil.isEmpty(spans)) {
            return ret;
        } else {
            return spans.get(0).text();
        }
    }

}
