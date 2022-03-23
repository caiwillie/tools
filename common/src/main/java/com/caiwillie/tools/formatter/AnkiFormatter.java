package com.caiwillie.tools.formatter;

import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.anki.model.AnkiCard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caiwillie
 */
public class AnkiFormatter {

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

}
