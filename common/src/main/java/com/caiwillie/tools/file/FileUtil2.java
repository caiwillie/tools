package com.caiwillie.tools.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public static void assertExtension(File path, String extension) {
        if(!StrUtil.equals(FileNameUtil.extName(path), extension)) {
            throw new IllegalArgumentException(StrUtil.format("文件 {} 的后缀不是 {}", path, extension));
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

    public static List<File> loopFiles(File file, String extension) {
        List<File> ret = new ArrayList<>();
        List<File> files = FileUtil.loopFiles(file, 1, pathname -> {
            // 后缀名是OPML, 并且是文件的话
            return StrUtil.equals(FileNameUtil.extName(pathname), extension) && pathname.isFile();
        });

        if(CollUtil.isNotEmpty(files)) {

            ret.addAll(files);
        }

        return ret;
    }



}
