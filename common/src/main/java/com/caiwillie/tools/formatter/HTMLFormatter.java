package com.caiwillie.tools.formatter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author caiwillie
 */
public class HTMLFormatter {

    private static final String CODE_TEMPLATE = "<xmp>{}</xmp>\n";

    private static final Pattern LESS_THEN_PATTERN = Pattern.compile("<");

    private static final String LESS_THEN = "&lt;";

    private static final Pattern GRATE_THEN_PATTERN = Pattern.compile(">");

    private static final String GRATE_THEN = "&gt;";

    private static final String P = "<p>{}</p>\n";

    private static final String BR = "{}</br>\n";

    private static final String INDENT = "&nbsp;&nbsp;&nbsp;&nbsp;";

    public static String wrapXMP(String str) {
        return StrUtil.format(CODE_TEMPLATE, str);
    }

    public static String wrapP(String str) {
        return StrUtil.format(P, str);
    }

    public static String appendBR(String str) {
        return StrUtil.format(BR, str);
    }

    public static String escapeAngleBracket (String str) {
        str = ReUtil.replaceAll(str, LESS_THEN_PATTERN, LESS_THEN);
        return ReUtil.replaceAll(str, GRATE_THEN_PATTERN, GRATE_THEN);
    }

    public static String formatIndent(List<String> list) {
        StringBuilder ret = new StringBuilder();
        if(CollUtil.isEmpty(list)) {
            return null;
        }


        for (int i = 0; i < list.size(); i++) {
            ret.append(wrapP(indent(i, list.get(i))));
        }


        return ret.toString();
    }

    public static String formatList(List<String> list) {
        StringBuilder ret = new StringBuilder();
        if(CollUtil.isEmpty(list)) {
            return null;
        }

        for (String str : list) {
            ret.append(wrapP(str));
        }

        return ret.toString();
    }

    private static String indent(int index, String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < index; i++) {
            sb.append(INDENT);
        }
        sb.append(str);
        return sb.toString();
    }

}
