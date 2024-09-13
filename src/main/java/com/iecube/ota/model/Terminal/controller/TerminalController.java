package com.iecube.ota.model.Terminal.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.Terminal.entity.TerminalEntity;
import com.iecube.ota.model.Terminal.service.TerminalService;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/device")
public class TerminalController extends BaseController {

    @Autowired
    private TerminalService terminalService;

    @PostMapping("/list")
    public JsonResult<List> getProductDevice(Integer productId){
        List<TerminalEntity> terminalEntityList = terminalService.getTerminalListByProductId(productId);
        return new JsonResult<>(OK, terminalEntityList);
    }

    @PostMapping("/upgrade")
    public JsonResult<TerminalEntity> devicePassiveUpgrade(String terminalDid,Long firmwareId){
        TerminalEntity terminalEntity = terminalService.devicePassiveUpgrade(terminalDid, firmwareId);
        return new JsonResult<>(OK, terminalEntity);
    }


}
