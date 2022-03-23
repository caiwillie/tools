package com.caiwillie.tools.formatter;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * @author caiwillie
 */
public class HTMLFormatter {

    private static String CODE_TEMPLATE = "<code>{}</code>";

    private static Pattern LESS_THEN_PATTERN = Pattern.compile("<");

    private static String LESS_THEN = "&lt;";

    private static Pattern GRATE_THEN_PATTERN = Pattern.compile(">");

    private static String GRATE_THEN = "&gt;";

    public static String wrapCode (String str) {
        return StrUtil.format(CODE_TEMPLATE, str);
    }

    public static String escapeAngleBracket (String str) {
        str = ReUtil.replaceAll(str, LESS_THEN_PATTERN, LESS_THEN);
        return ReUtil.replaceAll(str, GRATE_THEN_PATTERN, GRATE_THEN);
    }


}
