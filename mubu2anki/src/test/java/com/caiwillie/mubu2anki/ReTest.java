package com.caiwillie.mubu2anki;

import cn.hutool.core.util.ReUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

public class ReTest {

    private static final Pattern pattern =
            Pattern.compile("^(\\d{1,}(\\.\\d{1,}){0,} |（([\\u2E80-\\u9FFF]|\\w){1,}）)");

    @Test
    void test() {
        List<String> allGroups = ReUtil.getAllGroups(pattern, "（dasdas）dasdasda");
        String group0 = ReUtil.getGroup0(pattern, "dasdasda");
        return;
    }

}
