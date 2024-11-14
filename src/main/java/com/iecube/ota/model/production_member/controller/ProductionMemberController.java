package com.iecube.ota.model.production_member.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.production_member.entity.ProductionMember;
import com.iecube.ota.model.production_member.service.ProductionMemberService;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/p_member")
public class ProductionMemberController extends BaseController {

    @Autowired
    private ProductionMemberService productionMemberService;

    @GetMapping("/all")
    public JsonResult<List<ProductionMember>> getAll() {
        List<ProductionMember> productionMembers = productionMemberService.findAll();
        return new JsonResult<>(OK, productionMembers);
    }

    @PostMapping("/add")
    public JsonResult<List<ProductionMember>> add(@RequestBody List<ProductionMember> productionMemberList) {
        List<ProductionMember> productionMembers = productionMemberService.addProductionMember(productionMemberList, currentUser());
        return new JsonResult<>(OK, productionMembers);
    }

    @DeleteMapping("/del")
    public JsonResult<List<ProductionMember>> del(@RequestBody ProductionMember productionMember) {
        List<ProductionMember> productionMembers = productionMemberService.removeProductionMember(productionMember, currentUser());
        return new JsonResult<>(OK, productionMembers);
    }

}
