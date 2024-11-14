package com.iecube.ota.model.message.card;

import lombok.Data;

@Data
public class Card {
    private CardConfig config;
    private String header;
    private String elements;
}
