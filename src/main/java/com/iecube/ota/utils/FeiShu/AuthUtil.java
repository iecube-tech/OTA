package com.iecube.ota.utils.FeiShu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.model.User.Service.ex.AuthException;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.User.dto.UserInfoDto;
import com.iecube.ota.model.User.dto.UserLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AuthUtil {

    private static final String USER_ACCESS_TOKEN_URI = "https://open.feishu.cn/open-apis/authen/v1/access_token";

    private static final String USER_REFRESH_TOKEN_URL= "https://open.feishu.cn/open-apis/authen/v1/oidc/refresh_access_token";
    private static final String USER_INFO_URI = "https://open.feishu.cn/open-apis/authen/v1/user_info";

    private static final ThreadLocal<CurrentUserDto> LOCAL_USER = new ThreadLocal<>();

    private static final String USER_TOKEN_REDIS_KEY_PIX = "USER_TOKEN_";

    private static final String USER_REFRESH_TOKEN_REDIS_KEY_PIX = "USER_REFRESH_TOKEN_";

    /**
     * 登录
     * @param code 飞书临时授权码
     * @return
     */
    public static CurrentUserDto authorizeUserAccessToken(String code, String appAccessToken ){
        String url = USER_ACCESS_TOKEN_URI;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json; charset=utf-8");
        headers.set("Authorization", "Bearer " + appAccessToken);
        Map<String,String> reqBody = new HashMap<>();
        reqBody.put("grant_type", "authorization_code");
        reqBody.put("code", code);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(reqBody,headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        try{
            CheckFSResponseUtil.checkErrorResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            UserLoginDto userLoginDto = new UserLoginDto();
            userLoginDto.setUserAccessToken(jsonNode.get("data").get("access_token").asText());
            userLoginDto.setRefreshToken(jsonNode.get("data").get("refresh_token").asText());
            userLoginDto.setExpiresIn(jsonNode.get("data").get("expires_in").asLong());
            userLoginDto.setRefreshExpiresIn(jsonNode.get("data").get("refresh_expires_in").asLong());
            userLoginDto.setTokenType(jsonNode.get("data").get("token_type").asText());
            userLoginDto.setScope(jsonNode.get("data").get("scope")==null?null:jsonNode.get("data").get("scope").asText());
            CurrentUserDto currentUserDto = new CurrentUserDto();
            currentUserDto.setUserLoginDto(userLoginDto);
            return currentUserDto;
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ServiceException(e.getMessage()==null?"JSON 转换异常":e.getMessage());
        }
    }

    public static CurrentUserDto refreshUserAccessToken(CurrentUserDto currentUserDto, String appAccessToken){
        log.info("刷新用户token， appAccessToken:{}", appAccessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json; charset=utf-8");
        headers.set("Authorization", "Bearer " + appAccessToken);
        Map<String,String> reqBody = new HashMap<>();
        reqBody.put("grant_type", "refresh_token");
        reqBody.put("refresh_token", currentUserDto.getUserLoginDto().getRefreshToken());
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(reqBody,headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(USER_REFRESH_TOKEN_URL, HttpMethod.POST, entity, String.class);
        try{
            CheckFSResponseUtil.checkErrorResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            currentUserDto.getUserLoginDto().setRefreshToken(jsonNode.get("data").get("refresh_token").asText());
            currentUserDto.getUserLoginDto().setRefreshExpiresIn(jsonNode.get("data").get("refresh_expires_in").asLong());
            currentUserDto.getUserLoginDto().setUserAccessToken(jsonNode.get("data").get("access_token").asText());
            currentUserDto.getUserLoginDto().setExpiresIn(jsonNode.get("data").get("expires_in").asLong());
            currentUserDto.getUserLoginDto().setScope(jsonNode.get("data").get("scope")==null?null:jsonNode.get("data").get("scope").asText());
            log.info("已刷新 userAccessToken");
            return currentUserDto;
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ServiceException(e.getMessage()==null?"JSON 转换异常":e.getMessage());
        }
    }

    /**
     * 登录第二步，获取个人信息， 之后将userid 作为authorization
     * @param currentUserDto
     * @return
     */
    public static CurrentUserDto authorizeUserInfo(CurrentUserDto currentUserDto){
        String url=USER_INFO_URI;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Authorization", "Bearer " + currentUserDto.getUserLoginDto().getUserAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        try {
            CheckFSResponseUtil.checkErrorResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setName(jsonNode.get("data").get("name")==null?null:jsonNode.get("data").get("name").asText());
            userInfoDto.setEnName(jsonNode.get("data").get("en_name")==null?null:jsonNode.get("data").get("en_name").asText());
            userInfoDto.setAvatarUrl(jsonNode.get("data").get("avatar_url")==null?null:jsonNode.get("data").get("avatar_url").asText());
            userInfoDto.setAvatarThumb(jsonNode.get("data").get("avatar_thumb")==null?null:jsonNode.get("data").get("avatar_thumb").asText());
            userInfoDto.setAvatarMiddle(jsonNode.get("data").get("avatar_middle")==null?null:jsonNode.get("data").get("avatar_middle").asText());
            userInfoDto.setAvatarBig(jsonNode.get("data").get("avatar_big")==null?null:jsonNode.get("data").get("avatar_big").asText());
            userInfoDto.setOpenId(jsonNode.get("data").get("open_id")==null?null:jsonNode.get("data").get("open_id").asText());
            userInfoDto.setUnionId(jsonNode.get("data").get("union_id")==null?null:jsonNode.get("data").get("union_id").asText());
            userInfoDto.setEmail(jsonNode.get("data").get("email")==null?null:jsonNode.get("data").get("email").asText());
            userInfoDto.setEnterpriseEmail(jsonNode.get("data").get("enterprise_email")==null?null:jsonNode.get("data").get("enterprise_email").asText());
            userInfoDto.setUserId(jsonNode.get("data").get("user_id")==null?null:jsonNode.get("data").get("user_id").asText());
            userInfoDto.setMobile(jsonNode.get("data").get("mobile")==null?null:jsonNode.get("data").get("mobile").asText());
            userInfoDto.setTenantKey(jsonNode.get("data").get("tenant_key")==null?null:jsonNode.get("data").get("tenant_key").asText());
            userInfoDto.setEmployeeNo(jsonNode.get("data").get("employee_no")==null?null:jsonNode.get("data").get("employee_no").asText());
            currentUserDto.setUserInfoDto(userInfoDto);
            return currentUserDto;
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ServiceException(e.getMessage()==null?"JSON 转换异常":e.getMessage());
        }
    }


    public static boolean authed(String authorization, String appAccessToken, StringRedisTemplate redisTemplate){
        // 判断是否登录
        // 如果 token 还在有效期内，已登录
        // 如果token 不在有效期， refreshToken 还在有效期 刷新token
        // 如果token 不在有效期 refreshToken 不在有效期 登录失效
        Long tokenExpireIn = redisTemplate.getExpire(getUserTokenRedisKey(authorization))==null?null:redisTemplate.getExpire(getUserTokenRedisKey(authorization));
        Long refreshTokenExpireIn = redisTemplate.getExpire(getUserReFreshTokenRedisKey(authorization))==null?null:redisTemplate.getExpire(getUserReFreshTokenRedisKey(authorization));
        if(tokenExpireIn==null || refreshTokenExpireIn==null){
            log.warn("tokenExpireIn==null || refreshTokenExpireIn==null");
            return false;
        }
        if( tokenExpireIn > 1800){
            CurrentUserDto currentUserDto = getCurrentUserFromRedis(authorization, redisTemplate);
            LOCAL_USER.set(currentUserDto);
            return true;
        }
        // tokenExpireIn<=1800 刷新token
        if(refreshTokenExpireIn>=1800){
            log.info("刷新用户token");
            CurrentUserDto currentUserDto = getCurrentUserFromRedis(authorization, redisTemplate);
            flushUserToken(currentUserDto, appAccessToken, redisTemplate);
            return true;
        }else{
            log.warn("refreshToken 过期");
            return false;
        }
    }

    private static CurrentUserDto getCurrentUserFromRedis(String authorization, StringRedisTemplate redisTemplate) {
        String value = redisTemplate.opsForValue().get(getUserReFreshTokenRedisKey(authorization));
        try {
            return new ObjectMapper().readValue(value, CurrentUserDto.class);
        } catch (JsonProcessingException e) {
            throw new ServiceException("JSON转对象异常");
        }
    }

    public static void cache(CurrentUserDto currentUserDto, StringRedisTemplate redisTemplate){
        String value;
        try {
            value = new ObjectMapper().writeValueAsString(currentUserDto);
        } catch (JsonProcessingException e) {
            throw new ServiceException("对象转JSON异常");
        }
        redisTemplate.opsForValue().set(getUserTokenRedisKey(currentUserDto.getUserInfoDto().getUnionId()),
                    value,
                    currentUserDto.getUserLoginDto().getExpiresIn(),TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(getUserReFreshTokenRedisKey(currentUserDto.getUserInfoDto().getUnionId()),
                    value,
                    currentUserDto.getUserLoginDto().getRefreshExpiresIn(), TimeUnit.SECONDS);
        log.info("已登录: {}", currentUserDto.getUserInfoDto().getUnionId());
    }

    public static void flushUserToken(CurrentUserDto currentUserDto, String appAccessToken, StringRedisTemplate redisTemplate){
        CurrentUserDto newCurrentUserDto = refreshUserAccessToken(currentUserDto, appAccessToken);
        LOCAL_USER.set(newCurrentUserDto);
        cache(currentUserDto, redisTemplate);
    }

    public static CurrentUserDto getCurrentUser() {
        CurrentUserDto currentUserDto = LOCAL_USER.get();
        if (currentUserDto == null) {
            throw new AuthException("找不到登录信息");
        }
        return currentUserDto;
    }

    private static String getUserTokenRedisKey(String userId) {
        return (USER_TOKEN_REDIS_KEY_PIX + userId).toUpperCase();
    }

    private static String getUserReFreshTokenRedisKey(String userId) {
        return (USER_REFRESH_TOKEN_REDIS_KEY_PIX + userId).toUpperCase();
    }

    public static void clean(){
        LOCAL_USER.remove();
    }
}
