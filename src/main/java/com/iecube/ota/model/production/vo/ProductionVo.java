package com.iecube.ota.model.production.vo;

import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.model.production.entity.ProductionExamine;
import lombok.Data;

@Data
public class ProductionVo {
    private ProductionExamine productionExamine;
    private FirmwareVo firmwareVo;
}
