package com.caiwillie.tools.mubu2anki;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.ReUtil;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class ReUtilTest {

    @Test
    void test() {
        String all = ReUtil.replaceAll(null, "\\s{1,}", "");
        return;
    }

    @Test
    void test2() {
        String decode = URLDecoder.decode(null, StandardCharsets.UTF_8);
        return;
    }
}
