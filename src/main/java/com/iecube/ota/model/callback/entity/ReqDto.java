package com.iecube.ota.model.callback.entity;

import lombok.Data;

@Data
public class ReqDto {
    private String challenge; // 飞书回调配置
    private String token;
    private String type;
    private String schema; // 飞书回调请求参数
    private ReqHeader header;
    private ReqEvent event;
}
