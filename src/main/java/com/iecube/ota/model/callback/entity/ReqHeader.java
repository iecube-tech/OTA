package com.iecube.ota.model.callback.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqHeader {
    @JsonProperty("event_id")
    private String eventId;
    private String token;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("tenant_key")
    private String tenantKey;
    @JsonProperty("app_id")
    private String appId;
}
