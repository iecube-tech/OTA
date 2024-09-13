package com.iecube.ota.model.Terminal.mapper;

import com.iecube.ota.model.Terminal.entity.TerminalEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TerminalMapper {
    TerminalEntity getByDid(String did);

    Integer updateByDid(TerminalEntity terminalEntity);

    Integer updateStatusByDid(Integer status, String did);

    Integer insert(TerminalEntity terminalEntity);

    List<TerminalEntity> getByProductId(Integer productId);
}
