package com.caiwillie.mubu2anki.generator;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author caiwillie
 */
public class Generator {

    public static void generate(List<File> paths) {
        if(CollUtil.isEmpty(paths)) {
            return;
        }

        for (File path : paths) {
            if(!(path.exists() && path.isFile())) {
                continue;
            }

            String csvName = FileNameUtil.mainName(path.getName()) + ".csv";

            try (InputStream in = FileUtil.getInputStream(path);
                 Writer writer = IoUtil.getWriter(new FileOutputStream(new File(path.getParent(), csvName)), StandardCharsets.UTF_8)) {
                Opml opml = new OpmlParser().parse(in);
                /*MubuOutline outline = new MubuConverter().convert(opml, hasSN);
                Anki anki = new AnkiConverter().converter(outline);
                List<String[]> csv = AnkiConverter.toCSV(anki);*/
                CSVWriterBuilder builder = new CSVWriterBuilder(writer);
                ICSVWriter csvWriter = builder.withSeparator('\t').build();
                // csvWriter.writeAll(csv);
            } catch (IOException | OpmlParseException e) {
                e.printStackTrace();
            }
        }
    }
}
