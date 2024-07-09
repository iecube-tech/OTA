package com.iecube.ota.model.product.mapper;

import com.iecube.ota.model.product.entity.PNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    Integer insertNode(PNode pNode);

    Integer updateNode(PNode pNode);

    Integer delNode(PNode pNode);

    Integer batchDelNode(List<PNode> list);

    List<PNode> allNode();

    PNode pNodeById(long id);

    PNode pNodeByPId(long pId);
}
