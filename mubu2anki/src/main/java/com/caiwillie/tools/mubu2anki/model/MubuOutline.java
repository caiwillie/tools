package com.caiwillie.tools.mubu2anki.model;

import lombok.Data;

import java.util.List;

/**
 * @author caiwillie
 */
@Data
public class MubuOutline {

    private String id;

    private String text;

    private List<MubuImage> images;

    private List<MubuOutline> childern;
}
