package com.caiwillie.mubu2anki.generator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.mubu2anki.converter.MubuConverter;
import com.caiwillie.mubu2anki.model.MubuOutline;

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

    public static void generate(JCommander commander, List<File> paths) {
        if(CollUtil.isEmpty(paths)) {
            return;
        }

        for (File path : paths) {
            String csvName = FileNameUtil.mainName(path.getName()) + ".csv";

            try (Writer writer = IoUtil.getWriter(new FileOutputStream(new File(path.getParent(), csvName)), StandardCharsets.UTF_8)) {
                MubuConverter mubuConverter = new MubuConverter();
                MubuOutline mubuOutline = mubuConverter.convert(path);
                
//                CSVWriterBuilder builder = new CSVWriterBuilder(writer);
//                ICSVWriter csvWriter = builder.withSeparator('\t').build();
//                csvWriter.writeAll(null);
            } catch (IOException e) {
                commander.getConsole().println(StrUtil.format("写入文件 {} 错误", csvName));
            }
        }
    }
}
