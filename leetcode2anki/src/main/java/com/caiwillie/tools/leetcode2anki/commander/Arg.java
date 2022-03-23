package com.caiwillie.tools.leetcode2anki.commander;

import com.beust.jcommander.Parameter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Arg {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--input", description = "leetcode src文件路径")
    private String input;

    @Parameter(names = "--output", description = "输出路径")
    private String output;

    @Parameter(names = "--help", description = "显示帮助信息", help = true)
    private boolean help;
}
