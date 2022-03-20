package com.caiwillie.mubu2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.mubu2anki.formatter.FormatUtil;
import com.caiwillie.mubu2anki.model.Anki;
import com.caiwillie.mubu2anki.model.AnkiCard;
import com.caiwillie.mubu2anki.model.MubuOutline;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author caiwillie
 */
public class AnkiConverter {
    private static final Pattern BLANK_PATTERN = Pattern.compile("\\s{1,}");

    private final List<AnkiCard> cards = new ArrayList<>();
    private MubuOutline parent = null;
    private final LinkedList<MubuOutline> cs = new LinkedList<>();
    private final LinkedList<MubuOutline> ss = new LinkedList<>();

    public Anki converter(MubuOutline outline) {
        if (outline == null) {
            return null;
        }

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
        ret.setTag(ReUtil.replaceAll(outline.getText(), BLANK_PATTERN, ""));
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

        String sn = ss.get(0).getSn();
        AnkiCard ankiCard = new AnkiCard();
        ankiCard.setSn(ReUtil.replaceAll(sn, BLANK_PATTERN, ""));
        ankiCard.setFront(FormatUtil.formatIndent(front));
        ankiCard.setBack(FormatUtil.formatList(back));
        cards.add(ankiCard);
    }
}
