package com.caiwillie.mubu2anki.formatter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.mubu2anki.model.Anki;
import com.caiwillie.mubu2anki.model.AnkiCard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caiwillie
 */
public class FormatUtil {

    private static final String P = "<p>{}</p>";

    private static final String BR = "{}</br>";

    private static final String INDENT = "&nbsp;&nbsp;&nbsp;&nbsp;";

    public static String formatIndent(List<String> list) {
        StringBuilder ret = new StringBuilder();
        if(CollUtil.isEmpty(list)) {
            return null;
        }


        for (int i = 0; i < list.size(); i++) {
            ret.append(p(indent(i, list.get(i))));
        }


        return ret.toString();
    }

    public static String formatList(List<String> list) {
        StringBuilder ret = new StringBuilder();
        if(CollUtil.isEmpty(list)) {
            return null;
        }

        for (String str : list) {
            ret.append(p(str));
        }

        return ret.toString();
    }

    public static String indent(int index, String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < index; i++) {
            sb.append(INDENT);
        }
        sb.append(str);
        return sb.toString();
    }

    public static String p(String str) {
        return StrUtil.format(P, str);
    }

    public static String br(String str) {
        return StrUtil.format(BR, str);
    }

    public static List<String[]> toCSV(Anki anki) {
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
