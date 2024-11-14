package com.iecube.ota.model.production.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.model.production.vo.ProductionVo;

public interface ProductionService {
    void firmwareToProduction(Long firmwareId,String unionId, CurrentUserDto currentUserDto);

    JsonNode examineCallback(String messageId, Boolean res);

    ProductionVo getProduction(Long productId);

    void newProductionInform(ProductionVo productionVo);

}
