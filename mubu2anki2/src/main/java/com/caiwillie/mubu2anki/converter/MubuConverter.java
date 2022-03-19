package com.caiwillie.mubu2anki.converter;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import be.ceau.opml.entity.Outline;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.mubu2anki.model.MubuOutline;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.caiwillie.mubu2anki.common.Constant.LEFT_BRACKET;
import static com.caiwillie.mubu2anki.common.Constant.MUBU_TEXT;

/**
 * @author caiwillie
 */
public class MubuConverter {

    private static final Pattern SN_PATTERN = Pattern.compile("^(\\d{1,}(\\.\\d{1,}){0,} |（([\\u2E80-\\u9FFF]|\\w){1,}）)");

    private static final String SN_TEMPLATE = "{}_{}";

    private File path;

    private String title = null;

    private boolean hasSN = false;

    public MubuOutline convert(File path) {

        Opml opml = null;
        try (InputStream in = FileUtil.getInputStream(path)) {
            opml = new OpmlParser().parse(in);
        } catch (IOException exception) {
            throw new IllegalArgumentException(StrUtil.format("文件 {} 读取失败", path));
        } catch (OpmlParseException exception) {
            throw new IllegalArgumentException(StrUtil.format("文件 {} 不是opml格式", path));
        }

        title = StrUtil.trim(opml.getHead().getTitle());

        Assert.notNull(title, "文件 {} 文档标题不能为空", path);

        this.path = path;
        MubuOutline root = new MubuOutline();
        root.setText(title);

        List<MubuOutline> mubuOutlines = convert(opml.getBody().getOutlines());

        // 判断第一个是不是有序号，如果有的，需要处理sn
        hasSN = CollUtil.isNotEmpty(mubuOutlines) && StrUtil.isNotBlank(mubuOutlines.get(0).getSn());

        check(mubuOutlines, null);

        if(hasSN) {
            root.setSn(title);
            completeSN(mubuOutlines);
        }

        root.setChildern(mubuOutlines);

        return root;
    }

    private List<MubuOutline> convert(List<Outline> outlines) {
        List<MubuOutline> ret = new ArrayList<>();

        if(CollUtil.isEmpty(outlines)) {
            return ret;
        }

        for (Outline outline : outlines) {
            ret.add(convert(outline));
        }

        return ret;
    }

    private MubuOutline convert(Outline outline) {
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

    private void check(List<MubuOutline> mubuOutlines, String parentSN) {
        if(CollUtil.isEmpty(mubuOutlines)) {
            return;
        }


        int preSiblingSerial = -1;

        for (int i = 0; i < mubuOutlines.size(); i++) {
            MubuOutline mubuOutline = mubuOutlines.get(i);

            String sn = mubuOutline.getSn();
            String nextSN = null;
            String text = mubuOutline.getText();
            if(StrUtil.isBlank(text)) {
                throw new RuntimeException(StrUtil.format("文件 {} 中存在空行", path.getAbsolutePath()));
            }

            if(hasSN) {
                if (StrUtil.isNotBlank(sn)) {
                    // 去除text前面的sn前缀
                    mubuOutline.setText(text.substring(sn.length()));

                    if (sn.charAt(0) != LEFT_BRACKET) {

                        sn = sn.substring(0, sn.length() - 1);
                        nextSN = sn;
                        // 序号是x.x.x
                        int dotIndex = sn.lastIndexOf('.');

                        int serial = NumberUtil.parseInt(sn.substring(dotIndex + 1));

                        if(parentSN != null) {
                            // 并且父级不为空，并且也是x.x.x开头
                            if(!StrUtil.equals(sn.substring(0, dotIndex), parentSN)) {
                                throw new RuntimeException(StrUtil.format("文件 {} 中的序号 {} 和上级 {} 不匹配", path.getAbsolutePath(), sn, parentSN));
                            }
                        }

                        if(preSiblingSerial < serial) {
                            preSiblingSerial = serial;
                        } else {
                            throw new RuntimeException(StrUtil.format("文件 {} 中的序号 {} 必须递增", path.getAbsolutePath(), sn));
                        }

                    } else {
                        sn = sn.substring(1, sn.length() - 1);
                    }
                    mubuOutline.setSn(sn);
                } else if (CollUtil.isNotEmpty(mubuOutline.getChildern())) {
                    throw new RuntimeException(StrUtil.format("文件 {} 中的 {} 没有序号", path.getAbsolutePath(), text));
                }
            }

            check(mubuOutline.getChildern(), nextSN);
        }
    }

    private void completeSN(List<MubuOutline> mubuOutlines) {
        if(CollUtil.isEmpty(mubuOutlines)) {
            return;
        }

        for (MubuOutline mubuOutline : mubuOutlines) {
            mubuOutline.setSn(StrUtil.format(SN_TEMPLATE, title, mubuOutline.getSn()));
            completeSN(mubuOutline.getChildern());
        }
    }

}
