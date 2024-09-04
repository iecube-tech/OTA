package com.iecube.ota.model.firmware.service;

import com.iecube.ota.model.firmware.entity.Firmware;
import com.iecube.ota.model.firmware.vo.FirmwareVo;

import java.util.List;

public interface FirmwareService {
    List<FirmwareVo> getProductFirmware(Long productId);

    List<FirmwareVo> addFirmware(Firmware firmware);

}
