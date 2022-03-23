package com.caiwillie.tools.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;

/**
 * @author caiwillie
 */
public class FileUtil2 {

    private static final String WORK_DIR = System.getProperty("user.dir");

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


    public static File getAbsoluteFile(String path) {
        // 如果指定文件
        if(!FileUtil.isAbsolutePath(path)) {
            // 如果是相对路径， 转换成绝对路径
            return new File(WORK_DIR, path);
        } else {
            return new File(path);
        }
    }


}
