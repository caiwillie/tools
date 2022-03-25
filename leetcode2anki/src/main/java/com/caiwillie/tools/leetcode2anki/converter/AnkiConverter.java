package com.caiwillie.tools.leetcode2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.anki.model.AnkiCard;
import com.caiwillie.tools.formatter.AnkiFormatter;
import com.caiwillie.tools.leetcode2anki.model.Question;

import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

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

            String title = StrUtil.format(TITLE_TEMPLATE, question.getId(), question.getTitle());

            String front = div(
                    p(title),
                    rawHtml(question.getContent())
            ).renderFormatted();


            String back = pre(question.getCode()).renderFormatted();

            AnkiCard card = new AnkiCard();
            card.setSn(AnkiFormatter.removeBlank(sn));
            card.setFront(AnkiFormatter.replaceTAB(front));
            card.setBack(AnkiFormatter.replaceTAB(back));
            cards.add(card);
        }

        return ret;
    }


}
