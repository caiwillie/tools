package com.caiwillie.tools.mubu2anki.commander;

import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @author caiwillie
 */
@Data
public class InputPath {

    private File lastDir;

    private List<File> files;
}
