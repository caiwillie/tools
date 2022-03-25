package com.caiwillie.tools.mubu2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.anki.model.AnkiCard;
import com.caiwillie.tools.formatter.AnkiFormatter;
import com.caiwillie.tools.formatter.HTMLFormatter;
import com.caiwillie.tools.mubu2anki.model.MubuOutline;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author caiwillie
 */
public class AnkiConverter {

    private final List<AnkiCard> cards = new ArrayList<>();
    private MubuOutline parent = null;
    private String tag = null;
    private final LinkedList<MubuOutline> cs = new LinkedList<>();
    private final LinkedList<MubuOutline> ss = new LinkedList<>();

    private static final String SN_TEMPLATE = "{}_{}";

    public Anki converter(MubuOutline outline) {
        if (outline == null) {
            return null;
        }

        // 移除blank
        tag = AnkiFormatter.removeBlank(outline.getText());

        cs.push(outline);
        while(!cs.isEmpty()) {
            MubuOutline c = cs.peek();
            if(ss.peek() != c) {
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
            if(CollUtil.isNotEmpty(outline.getChildern())) {
                cs.push(outline);
            }
        }
    }

    private void add() {
        List<String> front = new ArrayList<>();
        for (int i = ss.size() - 1; i >= 0; i--) {
            front.add(ss.get(i).getText());
        }
        List<String> back = new ArrayList<>();
        for (MubuOutline outline : ss.get(0).getChildern()) {
            back.add(outline.getText());
        }

        String sn = StrUtil.format(SN_TEMPLATE, tag, ss.get(0).getId());

        AnkiCard ankiCard = new AnkiCard();
        // 移除blank
        ankiCard.setSn(AnkiFormatter.removeBlank(sn));
        ankiCard.setFront(AnkiFormatter.replaceTAB(HTMLFormatter.formatIndent(front)));
        ankiCard.setBack(AnkiFormatter.replaceTAB(HTMLFormatter.formatList(back)));
        cards.add(ankiCard);
    }
}
