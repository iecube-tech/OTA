package com.iecube.ota.model.firmware.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.firmware.entity.Firmware;
import com.iecube.ota.model.firmware.vo.FirmwareVo;

import java.util.List;

public interface FirmwareService {
    List<FirmwareVo> getProductFirmware(Long productId);

    List<FirmwareVo> addFirmware(Firmware firmware, CurrentUserDto currentUser);

    JsonNode examineFirmware(String messageId, Boolean res);

    FirmwareVo getByFirmwareId(Long firmwareId);
}
