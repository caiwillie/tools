package com.caiwillie.tools.leetcode2anki.generator;

import com.beust.jcommander.JCommander;
import com.caiwillie.tools.leetcode2anki.commander.ArgDto;
import com.caiwillie.tools.leetcode2anki.converter.LeetCodeConverter;
import com.caiwillie.tools.leetcode2anki.model.Question;

import java.util.List;

public class Generator {

    public static void generate(JCommander commander, ArgDto arg) {

        LeetCodeConverter leetCodeConverter = new LeetCodeConverter();
        List<Question> questions = leetCodeConverter.convert(arg);
        return;
    }

}
