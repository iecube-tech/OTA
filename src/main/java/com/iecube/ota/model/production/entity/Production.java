package com.iecube.ota.model.production.entity;

import lombok.Data;

@Data
public class Production {
    private Long id;
    private Long productId;
    private Long firmwareId;
    private Long examineId;
}
