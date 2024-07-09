package com.iecube.ota.model.resource.entity;

import com.iecube.ota.entity.BaseEntity;
import lombok.Data;


@Data
public class Resource extends BaseEntity {
    Long id;
    String name;
    String filename;
    String originFilename;
    String type;
}
