package com.iecube.ota.model.callback.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqEvent {
    private Object operator;
    private String token;
    private Object action;
    private String host;
    @JsonProperty("delivery_type")
    private String deliveryType;
    private Object context;
}
