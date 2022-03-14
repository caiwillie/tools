package com.caiwillie.mubu2anki.generator;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.caiwillie.mubu2anki.converter.AnkiConverter;
import com.caiwillie.mubu2anki.converter.MubuConverter;
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
public class Generator {

    public void generator(List<File> files, boolean hasSN) {
        if(CollUtil.isEmpty(files)) {
            return;
        }

        for (File file : files) {
            if(!(file.exists() && file.isFile())) {
                continue;
            }

            String csvName = FileNameUtil.mainName(file.getName()) + ".csv";

            try (InputStream in = FileUtil.getInputStream(file);
                 Writer writer = IoUtil.getWriter(new FileOutputStream(new File(file.getParent(), csvName)), StandardCharsets.UTF_8)) {
                Opml opml = new OpmlParser().parse(in);
                MubuOutline outline = new MubuConverter().convert(opml, hasSN);
                Anki anki = new AnkiConverter().converter(outline);
                List<String[]> csv = AnkiConverter.toCSV(anki);
                CSVWriterBuilder builder = new CSVWriterBuilder(writer);
                ICSVWriter csvWriter = builder.withSeparator('\t').build();
                csvWriter.writeAll(csv);
            } catch (IOException | OpmlParseException e) {
                e.printStackTrace();
            }
        }

    }
}
