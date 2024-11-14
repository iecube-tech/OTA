package com.iecube.ota.model.resource.entity;

import com.iecube.ota.entity.BaseEntity;
import lombok.Data;


@Data
public class Resource extends BaseEntity {
    private Long id;
    private String name;
    private String filename;
    private String originFilename;
    private String type;
    private Long size;
    private String md5;
    private String link;
}
