package com.iecube.ota.model.resource.entity;

import lombok.Data;

@Data
public class ResourceVo {
    Long id;
    String name;
    String filename;
    String originFilename;
    Long size;
    String md5;
    String link;
}
