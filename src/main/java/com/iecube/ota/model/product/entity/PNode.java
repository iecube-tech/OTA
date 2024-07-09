package com.iecube.ota.model.product.entity;

import lombok.Data;

import java.util.List;

@Data
public class PNode {
    private Long id;
    private Long pId;
    private String name;
    private Long type;  // 0 leaf 叶子  1 node
    List<PNode> children;
}
