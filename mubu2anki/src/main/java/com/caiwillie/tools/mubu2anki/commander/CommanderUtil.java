package com.caiwillie.tools.mubu2anki.commander;

import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.tools.file.FileUtil2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.caiwillie.tools.mubu2anki.common.Constant.OPML;

/**
 * @author caiwillie
 */
public class CommanderUtil {

    public static void parseHelp(JCommander commander, Arg arg) {
        if(arg.isHelp()) {
            commander.usage();
            System.exit(0);
        }
    }

    public static List<File> parseInput(JCommander commander, Arg arg) {
        // 最终需要扫描的文件
        List<File> ret = new ArrayList<>();

        String filePath = arg.getInput();
        if(StrUtil.isBlank(filePath)) {
            // 如果没指定文件 并且 参数也不为空
            commander.usage();
            System.exit(0);
        } else  {
            // 如果指定文件
            File path = FileUtil2.getAbsoluteFile(filePath);

            FileUtil2.assertExist(path);

            if(path.isFile()) {
                // 判断后缀
                FileUtil2.assertExtension(path, OPML);
                ret.add(path);
            } else {
                ret.addAll(FileUtil2.loopFiles(path, OPML));
            }
        }

        return ret;
    }

}
