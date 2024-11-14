package com.iecube.ota.model.FsAuthLogin.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.config.AppAccessTokenConfig;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.utils.FeiShu.AuthUtil;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class FsAuthLoginController extends BaseController {
    @Value("${feiShu.appId}")
    private String appId;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AppAccessTokenConfig appAccessTokenConfig;

    @GetMapping("/app_id")
    public JsonResult<String> getAppId(){
        return new JsonResult<>(OK, appId);
    }

    @GetMapping("/login")
    public JsonResult<CurrentUserDto> getUserIno(String code){
        CurrentUserDto currentUserDto = AuthUtil.authorizeUserAccessToken(code, appAccessTokenConfig.getAppAccessToken());
        CurrentUserDto result = AuthUtil.authorizeUserInfo(currentUserDto);
        AuthUtil.cache(result, stringRedisTemplate);
        return new JsonResult<>(OK, result);
    }

}
