package com.caiwillie.tools.formatter;

import cn.hutool.core.util.ReUtil;
import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.anki.model.AnkiCard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author caiwillie
 */
public class AnkiFormatter {

    private static final Pattern TAB_PATTERN = Pattern.compile("\\t");
    private static final String FOUR_SPACE = "    ";

    public static List<String[]> formatWithSN(Anki anki) {
        List<String[]> ret = new ArrayList<>();
        String tag = anki.getTag();
        for (AnkiCard card : anki.getCards()) {
            String[] arr = new String[4];
            arr[0] = card.getSn();
            arr[1] = card.getFront();
            arr[2] = card.getBack();
            arr[3] = tag;
            ret.add(arr);
        }
        return ret;
    }

    public static String replaceTAB(String str) {
        return ReUtil.replaceAll(str, TAB_PATTERN, FOUR_SPACE);
    }

}
