package com.caiwillie.tools.mubu2anki.converter;

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
import com.caiwillie.tools.mubu2anki.model.MubuImage;
import com.caiwillie.tools.mubu2anki.model.MubuOutline;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
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

import static com.caiwillie.tools.mubu2anki.common.Constant.*;

/**
 * @author caiwillie
 */
public class MubuConverter {

    private static final ObjectMapper OBJECT_MAPPER;

    private static final CollectionType IMAGE_ARRAY_TYPE;

    private static final Pattern SN_PATTERN = Pattern.compile("^(\\d{1,}(\\.\\d{1,}){0,} |（([\\u2E80-\\u9FFF]|\\w){1,}）)");

    private static final Pattern SN_PATTERN_2 = Pattern.compile("^\\d{1,}(\\.\\d{1,}){0,}$");

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        IMAGE_ARRAY_TYPE = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, MubuImage.class);
    }

    private File path;

    public MubuOutline convert(File path) {

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

        this.path = path;
        MubuOutline root = new MubuOutline();
        root.setText(title);

        List<MubuOutline> mubuOutlines = convert(opml.getBody().getOutlines());

        // 处理sn
        handleId(mubuOutlines, title);

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
        String mubuText = URLDecoder.decode(outline.getAttribute(MUBU_TEXT), StandardCharsets.UTF_8);
        String mubuImages = URLDecoder.decode(outline.getAttribute(MUBU_IMAGES), StandardCharsets.UTF_8);

        String id = getId(mubuText);
        List<MubuImage> images = getImages(mubuImages);

        MubuOutline ret = new MubuOutline();
        ret.setId(id);
        ret.setText(mubuText);
        ret.setImages(images);

        List<MubuOutline> children = convert(outline.getSubElements());
        ret.setChildern(children);
        return ret;
    }

    private String getId(String content) {
        // 将html格式的text内容解析成doc
        Document doc = Jsoup.parse(content);
        Elements spans = doc.getElementsByTag("span");

        String span0 = CollUtil.isEmpty(spans) ? null : StrUtil.trim(spans.get(0).text());
        return ReUtil.getGroup0(SN_PATTERN, span0);
    }

    private List<MubuImage> getImages(String content) {
        List<MubuImage> ret = new ArrayList<>();
        if(StrUtil.isBlank(content)) {
            return ret;
        }

        try {

            List<MubuImage> tempList = OBJECT_MAPPER.readValue(content, IMAGE_ARRAY_TYPE);

            if(CollUtil.isNotEmpty(tempList)) {
                for (MubuImage mubuImage : tempList) {
                    Assert.notNull(mubuImage.getUri(), "幕布图片地址不能为空");
                    Assert.isTrue(mubuImage.getW() > 0, "幕布图片宽度必须大于0");
                    ret.add(mubuImage);
                }
            }

            return ret;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("幕布图片列表解析失败");
        }

    }

    private void handleId(List<MubuOutline> mubuOutlines, String parentSN) {
        if(CollUtil.isEmpty(mubuOutlines)) {
            return;
        }

        int preSiblingSerial = -1;

        for (int i = 0; i < mubuOutlines.size(); i++) {
            MubuOutline mubuOutline = mubuOutlines.get(i);

            String rawId = mubuOutline.getId();
            String text = mubuOutline.getText();
            if(StrUtil.isBlank(text)) {
                throw new RuntimeException(StrUtil.format("文件 {} 中 父级 {} 下存在空行", path.getAbsolutePath(), parentSN));
            }

            String id = null;
            if (StrUtil.isNotBlank(rawId)) {
                if (rawId.charAt(0) != LEFT_BRACKET) {

                    id = rawId.substring(0, rawId.length() - 1);
                    // 序号是x.x.x
                    int dotIndex = rawId.lastIndexOf('.');

                    int serial = NumberUtil.parseInt(rawId.substring(dotIndex + 1));

                    if(ReUtil.isMatch(SN_PATTERN_2, parentSN)) {
                        // 并且父级不为空，并且也是x.x.x开头
                        if(!StrUtil.equals(rawId.substring(0, dotIndex), parentSN)) {
                            throw new RuntimeException(StrUtil.format("文件 {} 中的序号 {} 和上级 {} 不匹配", path.getAbsolutePath(), rawId, parentSN));
                        }
                    }

                    if(preSiblingSerial < serial) {
                        preSiblingSerial = serial;
                    } else {
                        throw new RuntimeException(StrUtil.format("文件 {} 中的序号 {} 必须递增", path.getAbsolutePath(), rawId));
                    }
                } else {
                    id = rawId.substring(1, rawId.length() - 1);
                }
                int index = StrUtil.indexOf(text, rawId, 0, false);
                text = StrUtil.replace(text, index, index + rawId.length(), "");
                mubuOutline.setText(text);
                mubuOutline.setId(id);
            } else if (CollUtil.isNotEmpty(mubuOutline.getChildern())) {
                throw new RuntimeException(StrUtil.format("文件 {} 中的 {} 没有序号, 上级是 {}", path.getAbsolutePath(), text, parentSN));
            }

            handleId(mubuOutline.getChildern(), rawId);
        }
    }
}
