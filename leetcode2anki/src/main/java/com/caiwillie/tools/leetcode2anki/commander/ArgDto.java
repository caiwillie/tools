package com.caiwillie.tools.leetcode2anki.commander;

import lombok.Data;

import java.io.File;

/**
 * @author caiwillie
 */
@Data
public class ArgDto {

    private File codePath;

    private File contentPath;

    public ArgDto(File codePath, File contentPath) {
        this.codePath = codePath;
        this.contentPath = contentPath;
    }
}
