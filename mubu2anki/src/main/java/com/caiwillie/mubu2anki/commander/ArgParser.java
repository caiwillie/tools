package com.caiwillie.mubu2anki.commander;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.mubu2anki.commander.Arg;
import com.caiwillie.mubu2anki.common.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.caiwillie.mubu2anki.common.Constant.OPML;

/**
 * @author caiwillie
 */
public class ArgParser {

    private static final String WORK_DIR = System.getProperty("user.dir");

    public static List<File> parseFiles(JCommander commander, Arg arg) {
        // 最终需要扫描的文件
        List<File> ret = new ArrayList<>();

        String filePath = arg.getFile();
        if(StrUtil.isBlank(filePath) && CollUtil.isNotEmpty(arg.getParameters())) {
            // 如果没指定文件 并且 参数也不为空
            commander.usage();
            System.exit(0);
        } else if (StrUtil.isNotBlank(filePath)) {
            // 如果指定文件
            if(!FileUtil.isAbsolutePath(filePath)) {
                // 如果是相对路径， 转换成绝对路径
                filePath = WORK_DIR + "/" + filePath;
            }

            File file = new File(filePath);

            if(StrUtil.isNotBlank(FileNameUtil.extName(filePath))) {
                ret.add(file);
            } else {
                ret.addAll(loopDir(file));
            }
        } else {
            ret.addAll(loopDir(new File(WORK_DIR)));
        }

        return ret;
    }

    public static boolean isSN(JCommander commander, Arg arg) {
        if(StrUtil.equals(arg.getFormat(), Constant.SN)) {
            return true;
        } else {
            return false;
        }
    }

    private static List<File> loopDir(File file) {
        List<File> ret = new ArrayList<>();
        List<File> files = FileUtil.loopFiles(file, 1, pathname -> {
            if (StrUtil.equals(FileNameUtil.extName(pathname), OPML)) {
                return true;
            }
            return false;
        });

        if(CollUtil.isNotEmpty(files)) {
            ret.addAll(files);
        }

        return ret;
    }

}
