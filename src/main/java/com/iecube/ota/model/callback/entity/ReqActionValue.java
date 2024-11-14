package com.iecube.ota.model.callback.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqActionValue {
    @JsonProperty("examine_res")
    private Integer examineRes;
}
