package com.iecube.ota.model.firmware.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iecube.ota.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class FirmwareVo {
    private Long id;
    private Long productId;
    private String productName;
    private String version;
    private String description;
    @JsonProperty("isFull")
    private Boolean isFull;
    private Long timestamp;
    private String filename;
    private String originFilename;
    private String type;
    private Long size;
    private String md5;
    private Date createTime;
    private String link;
    private String cdn;
    private String creator;
    private String examineUnionId;  // 审批人员的id
    private String examineStatus; // 审批状态
    private String examineMessageId; // 审批消息id
    private Date examineTime; // 审批时间
}
