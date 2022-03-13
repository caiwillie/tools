package com.caiwillie.mubu2anki.commander;

import com.beust.jcommander.Parameter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Arg {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--file", description = "opml 文件 或者 所在目录")
    private String file;

    @Parameter(names = "--format", description = "文件格式：normal（普通格式），sn（序号格式）")
    private String format;

    @Parameter(names = "--help", description = "显示帮助信息", help = true)
    private boolean help;
}
