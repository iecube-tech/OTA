package com.iecube.ota.model.admin.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.admin.entity.AdminMember;
import com.iecube.ota.model.admin.entity.AdminMemberQo;
import com.iecube.ota.model.admin.service.AdminService;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class  AdminController extends BaseController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/all")
    public JsonResult<List<AdminMember>> getAdminMem(){
        List<AdminMember> adminMemberList = adminService.getMembers();
        return new JsonResult<>(OK, adminMemberList);
    }

    @GetMapping("/is")
    public JsonResult<Boolean> isAdmin(){
        Boolean res = adminService.isMember(currentUser());
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/add")
    public JsonResult<List<AdminMember>> addAdmin(@RequestBody List<AdminMemberQo> adminMemberQoList){
        List<AdminMember> adminMemberList = adminService.addMember(adminMemberQoList, currentUser());
        return new JsonResult<>(OK, adminMemberList);
    }

    @PostMapping("/del")
    public JsonResult<List<AdminMember>> deleteAdmin(@RequestBody AdminMemberQo adminMemberQo){
        List<AdminMember> adminMemberList =  adminService.delMember(adminMemberQo, currentUser());
        return new JsonResult<>(OK, adminMemberList);
    }
}
