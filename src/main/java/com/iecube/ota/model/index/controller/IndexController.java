package com.iecube.ota.model.index.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.utils.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController extends BaseController {

    @GetMapping
    public JsonResult<String> index() {
        return new JsonResult<>(OK, "ok");
    }
}
