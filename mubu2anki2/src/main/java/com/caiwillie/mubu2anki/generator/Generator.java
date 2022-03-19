package com.caiwillie.mubu2anki.generator;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author caiwillie
 */
public class Generator {

    public static void generate(JCommander commander, List<File> paths) {
        if(CollUtil.isEmpty(paths)) {
            return;
        }

        for (File path : paths) {
            String csvName = FileNameUtil.mainName(path.getName()) + ".csv";

            try (Writer writer = IoUtil.getWriter(new FileOutputStream(new File(path.getParent(), csvName)), StandardCharsets.UTF_8)) {
                CSVWriterBuilder builder = new CSVWriterBuilder(writer);
                ICSVWriter csvWriter = builder.withSeparator('\t').build();
                csvWriter.writeAll(null);
            } catch (IOException e) {
                commander.getConsole().println(StrUtil.format("写入文件 {} 错误", csvName));
            }
        }
    }
}
