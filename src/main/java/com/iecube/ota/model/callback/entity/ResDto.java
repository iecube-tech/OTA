package com.iecube.ota.model.callback.entity;

import lombok.Data;

@Data
public class ResDto {
    private String challenge;
    private Toast toast;
    private Object card;
}
