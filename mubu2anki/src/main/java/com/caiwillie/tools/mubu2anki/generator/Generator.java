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
import java.util.List;

/**
 * @author caiwillie
 */
public class Generator {

    private static final String CSV_EXTENSION = ".csv";

    public static void generate(JCommander commander, List<File> paths, File output) {
        if(CollUtil.isEmpty(paths)) {
            return;
        }

        for (File path : paths) {
            String csvName = FileNameUtil.mainName(path.getName()) + CSV_EXTENSION;

            try (Writer writer = IoUtil.getWriter(new FileOutputStream(new File(output, csvName)), StandardCharsets.UTF_8)) {
                MubuConverter mubuConverter = new MubuConverter();
                MubuOutline mubuOutline = mubuConverter.convert(path);

                AnkiConverter ankiConverter = new AnkiConverter();
                Anki anki = ankiConverter.converter(mubuOutline);
                List<String[]> data = AnkiFormatter.formatWithSN(anki);
                CSVWriterBuilder builder = new CSVWriterBuilder(writer);
                ICSVWriter csvWriter = builder.withSeparator('\t').build();
                csvWriter.writeAll(data);
            } catch (IOException e) {
                commander.getConsole().println(StrUtil.format("写入文件 {} 错误", csvName));
            }
        }
    }
}
