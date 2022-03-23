package com.caiwillie.tools.leetcode2anki.commander;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.tools.file.FileUtil2;

import java.io.File;

/**
 * @author caiwillie
 */
public class CommanderUtil {

    private static final String CODE_SUB_PATH = "leetcode/editor/cn";

    private static final String CONTENT_SUB_PATH = "leetcode/editor/cn/doc/content/";

    public static void parseHelp(JCommander commander, Arg arg) {
        if(arg.isHelp()) {
            commander.usage();
            System.exit(0);
        }
    }

    public static InputPath parseInput(JCommander commander, Arg arg) {
        InputPath ret = null;

        String srcPath = arg.getInput();
        if(StrUtil.isBlank(srcPath)) {
            // 如果没指定文件 并且 参数也不为空
            commander.usage();
            System.exit(0);
        } else  {
            // 如果指定文件
            File path = FileUtil2.getAbsoluteFile(srcPath);

            FileUtil2.assertExist(path);

            FileUtil2.assertDirectory(path);

            File codePath = new File(path, CODE_SUB_PATH);

            FileUtil2.assertExist(codePath);

            FileUtil2.assertDirectory(codePath);

            File contentPath = new File(path, CONTENT_SUB_PATH);

            FileUtil2.assertExist(contentPath);

            FileUtil2.assertDirectory(contentPath);

            ret = new InputPath();
            ret.setCodePath(codePath);
            ret.setContentPath(contentPath);
            ret.setLastDir(path.getParentFile());

            return ret;
        }

        return ret;
    }

    public static File parseOutput(JCommander commander, Arg arg, File lastDir) {
        if(StrUtil.isBlank(arg.getOutput())) {
            return lastDir;
        }

        File output = FileUtil2.getAbsoluteFile(arg.getOutput());

        if(!FileUtil.exist(output)) {
            return FileUtil.mkParentDirs(output);
        } else {
            FileUtil2.assertDirectory(output);
            return output;
        }
    }

}