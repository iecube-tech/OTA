package com.iecube.ota.model.callback.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.callback.entity.ReqDto;
import com.iecube.ota.model.callback.entity.ResDto;
import com.iecube.ota.model.callback.service.CallbackService;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/callback")
public class CallbackController extends BaseController {

    @Autowired
    private CallbackService callbackService;

    @GetMapping("/curl")
    public JsonResult<String> curl(){
        return new JsonResult<>(OK,"ok");
    }

    @PostMapping("/fs")
    public ResponseEntity<ResDto> fs(@RequestBody ReqDto body){
        ResDto dto = new ResDto();
        dto.setChallenge(body.getChallenge());
        if(body.getChallenge()==null && body.getEvent()!=null){
            dto=callbackService.fs(body);
        }
        return ResponseEntity.ok(dto);
    }
}
