package com.iecube.ota.model.p_manager.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.p_manager.service.PManageService;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pm")
public class PManagerController extends BaseController {

    @Autowired
    private PManageService pManageService;

    @GetMapping("/{nodeId}")
    public JsonResult<String> getByNodeId(@PathVariable Long nodeId) {
        String res = pManageService.findByNodeId(nodeId);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/{nodeId}/add_pm")
    public JsonResult<String> addPMs(@PathVariable Long nodeId, @RequestBody List<String> pmList) {
        String res = pManageService.AddPM(nodeId, pmList);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/{nodeId}/add_manager")
    public JsonResult<String> addManagers(@PathVariable Long nodeId, @RequestBody List<String> managerList) {
        String res = pManageService.AddManager(nodeId, managerList);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/{nodeId}/add_developer")
    public JsonResult<String> addDevelopers(@PathVariable Long nodeId, @RequestBody List<String> developerList) {
        String res = pManageService.AddDeveloper(nodeId, developerList);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/assessing/officer/{nodeId}")
    public JsonResult<String> assessingOfficerByNodeId(@PathVariable Long nodeId) {
        String res = pManageService.assessingOfficerByNodeId(nodeId);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/developer/is/{nodeId}")
    public JsonResult<Boolean> isDeveloperByNodeId(@PathVariable Long nodeId) {
        Boolean res = pManageService.isDeveloper(nodeId, currentUser().getUserInfoDto().getUnionId());
        return new JsonResult<>(OK, res);
    }
}
