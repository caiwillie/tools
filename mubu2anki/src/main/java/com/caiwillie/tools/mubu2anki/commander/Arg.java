package com.caiwillie.tools.mubu2anki.commander;

import com.beust.jcommander.Parameter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Arg {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--input", description = "opml文件或者所在目录")
    private String input;

    @Parameter(names = "--output", description = "输出目录")
    private String output;

    @Parameter(names = "--help", description = "显示帮助信息", help = true)
    private boolean help;
}
