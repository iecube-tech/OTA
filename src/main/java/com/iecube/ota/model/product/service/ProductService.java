package com.iecube.ota.model.product.service;

import com.iecube.ota.model.product.entity.PNode;

import java.util.List;

public interface ProductService {

    void insertPNode(PNode pNode);

    PNode updatePNode(PNode pNode);

    void delNode(long id);

    PNode getAllProduct();

    PNode getTreeByNode(Long id);

    PNode getParentTreeByNode(Long id);

    List<PNode> getAncestorNode(Long id);
}
