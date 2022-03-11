package com.caiwillie.mubu2anki;

import be.ceau.opml.OpmlParseException;
import be.ceau.opml.OpmlParser;
import be.ceau.opml.entity.Opml;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.mubu2anki.commander.Args;
import com.caiwillie.mubu2anki.converter.AnkiConverter;
import com.caiwillie.mubu2anki.converter.MubuConverter;
import com.caiwillie.mubu2anki.model.Anki;
import com.caiwillie.mubu2anki.model.MubuOutline;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import static com.caiwillie.mubu2anki.common.Constant.OPML;

/**
 * @author caiwillie
 */
public class Main {

    private static final String WORK_DIR = System.getProperty("user.dir");

    public static void main(String[] argv) {
        Args args = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(args)
                .build();
        commander.setProgramName("mubu2anki");
        commander.parse(argv);

        // 最终需要扫描的文件
        List<File> files = new ArrayList<>();

        String filePath = args.getFile();
        if(StrUtil.isBlank(filePath) && CollUtil.isNotEmpty(args.getParameters())) {
            // 如果没指定文件 并且 参数也不为空
            commander.usage();
            return;
        } else if (StrUtil.isNotBlank(filePath)) {
            // 如果指定文件
            if(!FileUtil.isAbsolutePath(filePath)) {
                // 如果是相对路径， 转换成绝对路径
               filePath = WORK_DIR + "/" + filePath;
            }

            File file = new File(filePath);

            if(StrUtil.isNotBlank(FileNameUtil.extName(filePath))) {
                files.add(file);
            } else {
                files.addAll(loopDir(file));
            }
        } else {
            files.addAll(loopDir(new File(WORK_DIR)));
        }

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

    private static List<File> loopDir(File file) {
        List<File> ret = new ArrayList<>();
        List<File> files = FileUtil.loopFiles(file, 1, pathname -> {
            if (StrUtil.equals(FileNameUtil.extName(pathname), OPML)) {
                return true;
            }
            return false;
        });

        if(CollUtil.isNotEmpty(files)) {
            ret.addAll(files);
        }

        return ret;
    }
}
