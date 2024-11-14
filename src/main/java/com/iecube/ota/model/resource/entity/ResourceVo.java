package com.iecube.ota.model.resource.entity;

import lombok.Data;

@Data
public class ResourceVo {
    private Long id;
    private String name;
    private String filename;
    private String originFilename;
    private Long size;
    private String md5;
    private String link;
}
