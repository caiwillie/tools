package com.caiwillie.mubu2anki.model;

import lombok.Data;

import java.util.List;

/**
 * @author caiwillie
 */
@Data
public class MubuOutline {

    private String sn;

    private String text;

    private List<MubuOutline> childern;

}
