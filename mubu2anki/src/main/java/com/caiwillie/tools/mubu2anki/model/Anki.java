package com.caiwillie.tools.mubu2anki.model;

import lombok.Data;

import java.util.List;

/**
 * @author caiwillie
 */
@Data
public class Anki {

    private String tag;

    List<AnkiCard> cards;

}