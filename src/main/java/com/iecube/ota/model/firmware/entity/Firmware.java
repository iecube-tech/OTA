package com.iecube.ota.model.firmware.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Firmware {
    private Long id;
    private Long nodeId;
    private Long resourceId;
    private String version;
    private String description;
    @JsonProperty("isFull")
    private Boolean isFull;
    private long timestamp;
    private String cdn;
    private String creator;
    private String examineUnionId;  // 审批人员的id
    private String examineStatus; // 审批状态
    private String examineMessageId; // 审批消息id
    private Date examineTime; // 审批时间
}
