package com.caiwillie.mubu2anki;

import com.beust.jcommander.JCommander;
import com.caiwillie.mubu2anki.commander.Arg;
import com.caiwillie.mubu2anki.commander.CommanderUtil;
import com.caiwillie.mubu2anki.generator.Generator;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Arg arg = new Arg();
        JCommander commander = JCommander.newBuilder()
                .addObject(arg)
                .build();
        commander.setProgramName("mubu2anki");
        commander.parse(args);

        List<File> files = null;

        try {
            // 显示是否需要显示帮助
            CommanderUtil.parseHelp(commander, arg);

            // 解析需要扫描的文件
            files = CommanderUtil.parseFile(commander, arg);

        } catch (Exception e) {
            // 捕获异常，并且展示
            commander.getConsole().println(e.getMessage());
            System.exit(1);
        }

        Generator.generate(commander, files);
    }
}
