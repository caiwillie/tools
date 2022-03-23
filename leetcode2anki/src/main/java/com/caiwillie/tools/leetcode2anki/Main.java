package com.caiwillie.tools.leetcode2anki;

import com.beust.jcommander.JCommander;
import com.caiwillie.tools.leetcode2anki.commander.Arg;
import com.caiwillie.tools.leetcode2anki.commander.InputPath;
import com.caiwillie.tools.leetcode2anki.commander.CommanderUtil;
import com.caiwillie.tools.leetcode2anki.generator.Generator;

import java.io.File;

public class Main {

    private static String PROGRAM_NAME = "leetcode2anki";

    public static void main(String[] args) {
        Arg arg = new Arg();
        JCommander commander = JCommander.newBuilder()
                .addObject(arg)
                .build();
        commander.setProgramName(PROGRAM_NAME);
        commander.parse(args);

        InputPath inputPath = null;
        File output = null;
        try {
            // 显示是否需要显示帮助
            CommanderUtil.parseHelp(commander, arg);

            // 解析需要扫描的文件
            inputPath = CommanderUtil.parseSrc(commander, arg);

            output = CommanderUtil.parseOutput(commander, arg, inputPath.getRoot());

        } catch (Exception e) {
            // 捕获异常，并且展示
            commander.getConsole().println(e.getMessage());
            System.exit(1);
        }

        Generator.generate(commander, inputPath, output);
    }

}
