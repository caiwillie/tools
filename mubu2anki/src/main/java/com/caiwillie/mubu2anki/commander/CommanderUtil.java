package com.caiwillie.mubu2anki.commander;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.caiwillie.mubu2anki.common.Constant.OPML;

/**
 * @author caiwillie
 */
public class CommanderUtil {

    private static final String WORK_DIR = System.getProperty("user.dir");

    public static void parseHelp(JCommander commander, Arg arg) {
        if(arg.isHelp()) {
            commander.usage();
            System.exit(0);
        }
    }

    public static List<File> parseFile(JCommander commander, Arg arg) {
        // 最终需要扫描的文件
        List<File> ret = new ArrayList<>();

        String filePath = arg.getFile();
        if(StrUtil.isBlank(filePath)) {
            // 如果没指定文件 并且 参数也不为空
            commander.usage();
            System.exit(0);
        } else  {
            // 如果指定文件
            File path = null;
            if(!FileUtil.isAbsolutePath(filePath)) {
                // 如果是相对路径， 转换成绝对路径
                path = new File(WORK_DIR, filePath);
            } else {
                path = new File(filePath);
            }

            if(!path.exists()) {
                throw new IllegalArgumentException(StrUtil.format("路径 {} 不存在", path.getAbsolutePath()));
            }

            if(path.isFile()) {
                if(StrUtil.equals(FileNameUtil.extName(path), OPML)) {
                    ret.add(path);
                } else {
                    throw new IllegalArgumentException(StrUtil.format("文件 {} 的后缀不是opml", path.getAbsolutePath()));
                }
            } else {
                ret.addAll(loopDir(path));
            }
        }

        return ret;
    }

    private static List<File> loopDir(File file) {
        List<File> ret = new ArrayList<>();
        List<File> files = FileUtil.loopFiles(file, 1, pathname -> {
            // 后缀名是OPML, 并且是文件的话
            return StrUtil.equals(FileNameUtil.extName(pathname), OPML) && pathname.isFile();
        });

        if(CollUtil.isNotEmpty(files)) {

            ret.addAll(files);
        }

        return ret;
    }

}
