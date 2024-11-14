package com.iecube.ota.model.production.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ProductionExamine {
    private Long id;
    private Long productId;
    private Long firmwareId;
    private String creator;
    private Date createTime;
    private String examineUnionId;
    private String examineStatus; // 审批状态
    private String examineMessageId; // 审批消息id
    private Date examineTime; // 审批时间
}
