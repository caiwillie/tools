package com.caiwillie.tools.leetcode2anki.commander;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.tools.file.FileAssert;

import java.io.File;

/**
 * @author caiwillie
 */
public class CommanderUtil {

    private static final String CODE_SUB_PATH = "leetcode/editor/cn";

    private static final String CONTENT_SUB_PATH = "leetcode/editor/cn/doc/content/";

    private static final String WORK_DIR = System.getProperty("user.dir");

    public static void parseHelp(JCommander commander, Arg arg) {
        if(arg.isHelp()) {
            commander.usage();
            System.exit(0);
        }
    }

    public static ArgDto parseSrc(JCommander commander, Arg arg) {
        ArgDto ret = null;

        String srcPath = arg.getSrc();
        if(StrUtil.isBlank(srcPath)) {
            // 如果没指定文件 并且 参数也不为空
            commander.usage();
            System.exit(0);
        } else  {
            // 如果指定文件
            File path = null;
            if(!FileUtil.isAbsolutePath(srcPath)) {
                // 如果是相对路径， 转换成绝对路径
                path = new File(WORK_DIR, srcPath);
            } else {
                path = new File(srcPath);
            }

            FileAssert.assertExist(path);

            FileAssert.assertDirectory(path);

            File codePath = new File(path, CODE_SUB_PATH);

            FileAssert.assertExist(codePath);

            FileAssert.assertDirectory(codePath);

            File contentPath = new File(path, CONTENT_SUB_PATH);

            FileAssert.assertExist(contentPath);

            FileAssert.assertDirectory(contentPath);

            ret = new ArgDto(codePath, contentPath);
        }

        return ret;
    }




}