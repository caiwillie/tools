package com.caiwillie.mubu2anki;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.mubu2anki.commander.Arg;
import com.caiwillie.mubu2anki.commander.ArgParser;
import com.caiwillie.mubu2anki.converter.AnkiConverter;
import com.caiwillie.mubu2anki.converter.MubuConverter;
import com.caiwillie.mubu2anki.generator.Generator;
import com.caiwillie.mubu2anki.model.Anki;
import com.caiwillie.mubu2anki.model.MubuOutline;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author caiwillie
 */
public class Main {



    public static void main(String[] argv) {
        Arg arg = new Arg();
        JCommander commander = JCommander.newBuilder()
                .addObject(arg)
                .build();
        commander.setProgramName("mubu2anki");
        commander.parse(argv);

        // 显示是否需要显示帮助
        ArgParser.parseHelp(commander, arg);

        // 最终需要扫描的文件
        List<File> files = ArgParser.parseFiles(commander, arg);

        boolean hasSN = ArgParser.hasSN(commander, arg);

        // 生成
        Generator generator = new Generator();
        generator.generator(files, hasSN);
    }


}
