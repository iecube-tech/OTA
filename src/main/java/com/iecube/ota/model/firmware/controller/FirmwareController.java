package com.iecube.ota.model.firmware.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.firmware.entity.Firmware;
import com.iecube.ota.model.firmware.service.FirmwareService;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firmware")
public class FirmwareController extends BaseController {
    @Autowired
    private FirmwareService firmwareService;

    @GetMapping("/list/{id}")
    public JsonResult<List> getProductFirmware(@PathVariable Long id){
        List<FirmwareVo> firmwareVoList = firmwareService.getProductFirmware(id);
        return new JsonResult<>(OK, firmwareVoList);
    }

    @PostMapping("/add")
    public JsonResult<List> addFirmware(@RequestBody Firmware firmware){
        List<FirmwareVo> firmwareVoList = firmwareService.addFirmware(firmware, currentUser());
        return new JsonResult<>(OK, firmwareVoList);
    }
}
