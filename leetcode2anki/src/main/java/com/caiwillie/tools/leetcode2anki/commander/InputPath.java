package com.caiwillie.tools.leetcode2anki.commander;

import lombok.Data;

import java.io.File;

/**
 * @author caiwillie
 */
@Data
public class InputPath {

    private File lastDir;

    private File codePath;

    private File contentPath;
}
