package com.iecube.ota.model.firmware.entity;

import lombok.Data;

@Data
public class Firmware {
    private Long id;
    private Long nodeId;
    private Long resourceId;
    private String version;
    private String description;
    private long timestamp;
    private String cdn;
}
