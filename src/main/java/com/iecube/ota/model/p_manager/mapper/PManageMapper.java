package com.iecube.ota.model.p_manager.mapper;

import com.iecube.ota.model.p_manager.entity.PManage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PManageMapper {
    Integer insert(PManage pManage);

    Integer delete(PManage pManage);

    PManage select(Long id);

    List<PManage> selectByNodeId(Long nodeId);

    List<PManage> selectByNodeIds(List<Long> nodeIds);
}
