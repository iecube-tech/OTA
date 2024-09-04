package com.iecube.ota.model.product.qo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NodeQo {
    private Long id;
    @JsonProperty("pId")
    private Long pId;
    private String name;
    private Long type;  // 0 leaf 叶子  1 node
}
