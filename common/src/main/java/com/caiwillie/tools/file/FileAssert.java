package com.caiwillie.tools.file;

import cn.hutool.core.util.StrUtil;

import java.io.File;

/**
 * @author caiwillie
 */
public class FileAssert {

    public static void assertExist(File path) {
        if(!path.exists()) {
            throw new IllegalArgumentException(StrUtil.format("路径 {} 不存在", path.getAbsolutePath()));
        }
    }

    public static void assertDirectory(File path) {
        if(!path.isDirectory()) {
            throw new IllegalArgumentException(StrUtil.format("路径 {} 不是文件", path.getAbsolutePath()));
        }
    }

}
