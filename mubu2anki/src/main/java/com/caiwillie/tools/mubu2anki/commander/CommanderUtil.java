package com.caiwillie.tools.mubu2anki.commander;

import cn.hutool.core.io.FileUtil;
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

    public static InputPath parseInput(JCommander commander, Arg arg) {
        InputPath ret = new InputPath();

        // 最终需要扫描的文件
        List<File> files = new ArrayList<>();

        ret.setFiles(files);

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
                files.add(path);
                ret.setLastDir(path.getParentFile());
            } else {
                files.addAll(FileUtil2.loopFiles(path, OPML));
                ret.setLastDir(path);
            }
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
