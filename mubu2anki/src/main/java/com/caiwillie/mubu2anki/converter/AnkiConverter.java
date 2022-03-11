package com.caiwillie.mubu2anki.converter;

import cn.hutool.core.collection.CollUtil;
import com.caiwillie.mubu2anki.formatter.Formatter;
import com.caiwillie.mubu2anki.model.Anki;
import com.caiwillie.mubu2anki.model.AnkiCard;
import com.caiwillie.mubu2anki.model.MubuOutline;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author caiwillie
 */
public class AnkiConverter {


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

        String tag = outline.getText();

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
        AnkiCard ankiCard = new AnkiCard();
        ankiCard.setFront(Formatter.formatIndent(front));
        ankiCard.setBack(Formatter.formatList(back));
        cards.add(ankiCard);
        return;
    }

    public static List<String[]> toCSV(Anki anki) {
        List<String[]> ret = new ArrayList<>();
        String tag = anki.getTag();
        for (AnkiCard card : anki.getCards()) {
            String[] arr = new String[3];
            arr[0] = card.getFront();
            arr[1] = card.getBack();
            arr[2] = tag;
            ret.add(arr);
        }
        return ret;
    }
}
