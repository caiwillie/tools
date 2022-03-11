package com.caiwillie.mubu2anki;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.mubu2anki.commander.Args;
import com.caiwillie.mubu2anki.converter.AnkiConverter;
import com.caiwillie.mubu2anki.converter.MubuConverter;
import com.caiwillie.mubu2anki.model.Anki;
import com.caiwillie.mubu2anki.model.MubuOutline;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author caiwillie
 */
public class Main {

    public static void main(String[] argv) {
        Args args = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(args)
                .build();
        commander.setProgramName("mubu2anki");

        commander.parse(argv);

        if(StrUtil.isBlank(args.getFile()) && CollUtil.isNotEmpty(args.getParameters())) {
            commander.usage();
            return;
        } else if (StrUtil.isNotBlank(args.getFile())) {

        } else {

        }





        Resource resource = ResourceUtil.getResourceObj("软件架构设计.opml");
        String name = resource.getName();
        String csvName = String.join(".", name.substring(0, name.lastIndexOf(".")), "csv");

        try (InputStream in = resource.getStream();
             FileWriter writer = new FileWriter(new File("/home/caiwillie/anki", csvName))) {
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
