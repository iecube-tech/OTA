package com.iecube.ota.model.production.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.production.qo.ProductionQo;
import com.iecube.ota.model.production.service.ProductionService;
import com.iecube.ota.model.production.vo.ProductionVo;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production")
public class ProductionController extends BaseController {

    /** todo
     * 1. 提交转生产审批 =>发送审批消息
     * 2. 审批 => 发送生产版本更新消息 <== 获取生产团队人员unionIdList
     * 3. 查询生产版本
     */
    @Autowired
    private ProductionService productionService;

    @PostMapping("/push")
    public JsonResult<Void> firmwareToProduction(@RequestBody ProductionQo productionQo) {
        productionService.firmwareToProduction(productionQo.getFirmwareId(), productionQo.getUnionId(), currentUser());
        return new JsonResult<>(OK);
    }

    @GetMapping("/get/{productId}")
    public JsonResult<ProductionVo> getProductionFirmware(@PathVariable Long productId) {
        ProductionVo productionVo = productionService.getProduction(productId);
        return new JsonResult<>(OK, productionVo);
    }
}
