package com.caiwillie.tools.mubu2anki.generator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.formatter.AnkiFormatter;
import com.caiwillie.tools.mubu2anki.converter.AnkiConverter;
import com.caiwillie.tools.mubu2anki.converter.MubuConverter;
import com.caiwillie.tools.mubu2anki.model.MubuOutline;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author caiwillie
 */
public class Generator {

    private static final String CSV_EXTENSION = ".csv";

    public static void generate(JCommander commander, List<File> paths, File output, boolean separated) {
        if(CollUtil.isEmpty(paths)) {
            return;
        }

        LinkedHashMap<String, List<String[]>> dataMap = new LinkedHashMap<>();

        for (File path : paths) {
            String name = FileNameUtil.mainName(path.getName());
            // String csvName =  + CSV_EXTENSION;

            MubuConverter mubuConverter = new MubuConverter();
            MubuOutline mubuOutline = mubuConverter.convert(path);

            AnkiConverter ankiConverter = new AnkiConverter();
            Anki anki = ankiConverter.converter(mubuOutline);
            List<String[]> data = AnkiFormatter.formatWithSN(anki);
            dataMap.put(name, data);
        }

        if(CollUtil.isEmpty(dataMap)) {
            return;
        }

        if(separated) {
            // 每个生成文件是独立的
            for (Map.Entry<String, List<String[]>> entry : dataMap.entrySet()) {
                String name = entry.getKey();
                List<String[]> data = entry.getValue();
                String csvName = name + CSV_EXTENSION;
                try (Writer writer = IoUtil.getWriter(new FileOutputStream(new File(output, csvName)), StandardCharsets.UTF_8)) {
                    CSVWriterBuilder builder = new CSVWriterBuilder(writer);
                    ICSVWriter csvWriter = builder.withSeparator('\t').build();
                    csvWriter.writeAll(data);
                } catch (IOException e) {
                    commander.getConsole().println(StrUtil.format("写入文件 {} 错误", csvName));
                }
            }
        } else {
            // 合并到一起
            List<String[]> data = dataMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
            String csvName = output.getName() + CSV_EXTENSION;
            try (Writer writer = IoUtil.getWriter(new FileOutputStream(new File(output, csvName)), StandardCharsets.UTF_8)) {
                CSVWriterBuilder builder = new CSVWriterBuilder(writer);
                ICSVWriter csvWriter = builder.withSeparator('\t').build();
                csvWriter.writeAll(data);
            } catch (IOException e) {
                commander.getConsole().println(StrUtil.format("写入文件 {} 错误", csvName));
            }

        }
    }
}
