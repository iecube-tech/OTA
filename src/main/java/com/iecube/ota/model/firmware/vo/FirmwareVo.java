package com.iecube.ota.model.firmware.vo;

import com.iecube.ota.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class FirmwareVo {
    Integer productId;
    String productName;
    String version;
    String description;
    Long timestamp;
    String filename;
    String originFilename;
    String type;
    Long size;
    String md5;
    Date createTime;
    String link;
    String cdn;
}
