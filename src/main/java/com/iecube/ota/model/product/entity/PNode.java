package com.iecube.ota.model.product.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PNode {
    private Long id;
    @JsonProperty("pId")
    private Long pId;
    private String name;
    private Long type;  // 0 leaf 叶子  1 node
    private Long edit; // 0 1:启用编辑
    private Long deep;
    List<PNode> children;
}
