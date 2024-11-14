package com.iecube.ota.model.firmware.mapper;

import com.iecube.ota.model.firmware.entity.Firmware;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FirmwareMapper {
    Integer insert(Firmware firmware);

    List<FirmwareVo> getProductFirmware(Long productId);

    FirmwareVo getByFirmwareId(Long id);

    Integer updateFirmware(Firmware firmware);

    Firmware getFirmwareByExamineMessageId(String examineMessageId);

}
