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

        // 最终需要扫描的文件
        List<File> files = ArgParser.parseFiles(commander, arg);

        boolean isSN = ArgParser.isSN(commander, arg);

        for (File file : files) {
            if(!(file.exists() && file.isFile())) {
                continue;
            }

            String csvName = FileNameUtil.mainName(file.getName()) + ".csv";



            try (InputStream in = FileUtil.getInputStream(file);
                 Writer writer = IoUtil.getWriter(new FileOutputStream(new File(file.getParent(), csvName)), StandardCharsets.UTF_8)) {
                Opml opml = new OpmlParser().parse(in);
                MubuOutline outline = new MubuConverter().convert(opml);
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
