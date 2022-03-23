package com.caiwillie.tools.leetcode2anki.generator;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.JCommander;
import com.caiwillie.tools.anki.model.Anki;
import com.caiwillie.tools.formatter.AnkiFormatter;
import com.caiwillie.tools.leetcode2anki.commander.ArgDto;
import com.caiwillie.tools.leetcode2anki.converter.AnkiConverter;
import com.caiwillie.tools.leetcode2anki.converter.LeetCodeConverter;
import com.caiwillie.tools.leetcode2anki.model.Question;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Generator {

    public static void generate(JCommander commander, ArgDto arg) {

        LeetCodeConverter leetCodeConverter = new LeetCodeConverter();
        List<Question> questions = leetCodeConverter.convert(arg);

        AnkiConverter ankiConverter = new AnkiConverter();
        Anki anki = ankiConverter.convert(questions);

        List<String[]> data = AnkiFormatter.formatWithSN(anki);

        File root = arg.getRoot();

        String csvName = "leetcode.csv";

        try (Writer writer = IoUtil.getWriter(new FileOutputStream(new File(root.getParentFile(), csvName)), StandardCharsets.UTF_8)) {
            CSVWriterBuilder builder = new CSVWriterBuilder(writer);
            ICSVWriter csvWriter = builder.withSeparator('\t').build();
            csvWriter.writeAll(data);
        } catch (Exception e) {
            commander.getConsole().println(StrUtil.format("写入文件 {} 错误", csvName));
        }
    }

}
