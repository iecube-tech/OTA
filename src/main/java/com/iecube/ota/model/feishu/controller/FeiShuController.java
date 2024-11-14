package com.iecube.ota.model.feishu.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.feishu.service.FeiShuService;
import com.iecube.ota.model.feishu.vo.Department;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fs")
public class FeiShuController extends BaseController {

    @Autowired
    private FeiShuService feiShuService;

    @GetMapping("/departments")
    public JsonResult<String> getAllDepartments(){
        String res = feiShuService.getAllDepartments();
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/members/{departmentId}")
    public JsonResult<String> getAllUsers(@PathVariable("departmentId") String departmentId){
        String res = feiShuService.getDepartmentMember(departmentId);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/callback")
    public JsonResult<String> callback(){

        return new JsonResult<>(OK);
    }
}
