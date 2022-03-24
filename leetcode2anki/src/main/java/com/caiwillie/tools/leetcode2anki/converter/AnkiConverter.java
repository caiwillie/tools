package com.caiwillie.tools.leetcode2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.anki.model.AnkiCard;
import com.caiwillie.tools.formatter.AnkiFormatter;
import com.caiwillie.tools.formatter.HTMLFormatter;
import com.caiwillie.tools.leetcode2anki.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caiwillie
 */
public class AnkiConverter {

    private static String TAG = "leetcode";

    private static String SN_TEMPLATE = "{}_{}";

    private static String TITLE_TEMPLATE = "{}. {}";

    public Anki convert(List<Question> questions) {
        Anki ret = new Anki();
        ret.setTag(TAG);
        List<AnkiCard> cards = new ArrayList<>();
        ret.setCards(cards);

        if(CollUtil.isEmpty(questions)) {
            return ret;
        }

        for (Question question : questions) {
            String sn = StrUtil.format(SN_TEMPLATE, TAG, question.getId());
            String title = HTMLFormatter.wrapP(StrUtil.format(TITLE_TEMPLATE, question.getId(), question.getTitle()));
            String front = StrUtil.join("", title, question.getContent());
            String back = HTMLFormatter.wrapXMP(question.getCode());

            AnkiCard card = new AnkiCard();
            card.setSn(AnkiFormatter.removeBlank(sn));
            card.setFront(AnkiFormatter.replaceTAB(front));
            card.setBack(AnkiFormatter.replaceTAB(back));
            cards.add(card);
        }

        return ret;
    }


}
