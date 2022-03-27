package com.caiwillie.tools.mubu2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.anki.model.AnkiCard;
import com.caiwillie.tools.formatter.AnkiFormatter;
import com.caiwillie.tools.mubu2anki.model.MubuImage;
import com.caiwillie.tools.mubu2anki.model.MubuOutline;
import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;

import java.util.*;

import static j2html.TagCreator.*;

/**
 * @author caiwillie
 */
public class AnkiConverter {

    private static final String INDENT = "&nbsp;&nbsp;&nbsp;&nbsp;";
    private static final String SN_TEMPLATE = "{}_{}";
    private static final String MUBU_OUTLINE_CONTAINER_CLASSS = "mubu_outline_container";
    private static final String MUBU_OUTLINE_CLASS = "mubu_outline";
    private static final String IMAGE_STYLE_TEMPLATE = "width: {}px; height: auto;";
    private static final String IMAGE_URL_TEMPLATE = "https://api2.mubu.com/v3/{}";



    private final List<AnkiCard> cards = new ArrayList<>();
    private MubuOutline parent = null;
    private String tag = null;
    private final LinkedList<MubuOutline> cs = new LinkedList<>();
    private final LinkedList<MubuOutline> ss = new LinkedList<>();

    public Anki converter(MubuOutline outline) {
        if (outline == null) {
            return null;
        }

        // 移除blank
        tag = AnkiFormatter.removeBlank(outline.getText());

        cs.push(outline);
        while (!cs.isEmpty()) {
            MubuOutline c = cs.peek();
            if (ss.peek() != c) {
                ss.push(c);
                parent = c;
                push();
                add();
            } else {
                cs.pop();
                ss.pop();
            }
        }

        Anki ret = new Anki();
        ret.setTag(tag);
        ret.setCards(cards);

        return ret;
    }

    private void push() {
        List<MubuOutline> outlines = parent.getChildern();
        // 从后网前遍历，保证排在前面的元素先出栈
        for (int i = outlines.size() - 1; i >= 0; i--) {
            MubuOutline outline = outlines.get(i);
            if (CollUtil.isNotEmpty(outline.getChildern())) {
                cs.push(outline);
            }
        }
    }

    private void add() {
        String sn = StrUtil.format(SN_TEMPLATE, tag, ss.get(0).getId());
        String front = formatFront();
        String back = formatBack();

        AnkiCard ankiCard = new AnkiCard();
        // 移除blank
        ankiCard.setSn(AnkiFormatter.removeBlank(sn));
        ankiCard.setFront(AnkiFormatter.replaceTAB(front));
        ankiCard.setBack(AnkiFormatter.replaceTAB(back));
        cards.add(ankiCard);
    }

    private String formatFront() {
        List<DivTag> items = new ArrayList<>();
        for (int i = ss.size() - 1; i >= 0; i--) {
            items.add(formatOutline(ss.get(i)));
        }

        List<DivTag> divs = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            DivTag div = items.get(i);
            // 增加缩进
            divs.add(div(join(indent(i), items.get(i))).withClass(MUBU_OUTLINE_CONTAINER_CLASSS));
        }

        return div(divs.toArray(new DomContent[0])).renderFormatted();
    }

    private String formatBack() {
        List<DivTag> divs = new ArrayList<>();
        for (MubuOutline outline : ss.get(0).getChildern()) {
            divs.add(formatOutline(outline).withClass(MUBU_OUTLINE_CONTAINER_CLASSS));
        }

        return div(divs.toArray(new DomContent[0])).renderFormatted();
    }

    private static String indent(int index) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < index; i++) {
            sb.append(INDENT);
        }
        sb.append("\n");
        return sb.toString();
    }

    private DivTag formatOutline(MubuOutline mubuOutline) {
        String text = mubuOutline.getText();
        List<MubuImage> images = Optional.ofNullable(mubuOutline.getImages()).orElse(Collections.emptyList());

        return
                div(
                        div(rawHtml(text)),
                        ul(
                                each(images, image -> li
                                        (img()
                                        .withAlt("image")
                                        .withStyle(StrUtil.format(IMAGE_STYLE_TEMPLATE, image.getW()))
                                        .withSrc(StrUtil.format(IMAGE_URL_TEMPLATE, image.getUri())))
                                )
                        )
                ).withClass(MUBU_OUTLINE_CLASS);
    }
}
