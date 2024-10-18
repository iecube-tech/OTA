package com.iecube.ota.model.FsAuthLogin.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.utils.FeiShu.Auth;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class FsAuthLoginController extends BaseController {
    private Auth auth=new Auth();

    @GetMapping("/app_id")
    public JsonResult<String> getAppId(){
        String id = auth.getAppId();
        return new JsonResult<>(OK, id);
    }

    @GetMapping("/callback")
    public JsonResult<String> getUserIno(String code){
        System.out.println("开始调用");
        System.out.println(auth);
        this.auth.authorizeUserAccessToken(code);
        String userInfo = auth.getUserInfo();
        System.out.println("userInfo");
        System.out.println(userInfo);
        return new JsonResult<>(OK, userInfo);
    }



}
