package com.caiwillie.tools.mubu2anki;

import com.beust.jcommander.JCommander;
import com.caiwillie.tools.mubu2anki.commander.Arg;
import com.caiwillie.tools.mubu2anki.commander.CommanderUtil;
import com.caiwillie.tools.mubu2anki.commander.InputPath;
import com.caiwillie.tools.mubu2anki.generator.Generator;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Arg arg = new Arg();
        JCommander commander = JCommander.newBuilder()
                .addObject(arg)
                .build();
        commander.setProgramName("mubu2anki");
        commander.parse(args);

        InputPath input = null;
        File output = null;
        try {
            // 显示是否需要显示帮助
            CommanderUtil.parseHelp(commander, arg);

            // 解析需要扫描的文件
            input = CommanderUtil.parseInput(commander, arg);
            output = CommanderUtil.parseOutput(commander, arg, input.getLastDir());
        } catch (Exception e) {
            // 捕获异常，并且展示
            commander.getConsole().println(e.getMessage());
            System.exit(1);
        }

        Generator.generate(commander, input.getFiles(), output, arg.isSeparated());
    }
}
