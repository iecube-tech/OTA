package com.iecube.ota.model.User.dto;

import lombok.Data;

/**
 * 登录飞书后，返回的token 和 refreshToken
 * 用于登录功能
 */
@Data
public class UserLoginDto {
    String userAccessToken;
    String refreshToken;
    String tokenType="Bearer";
    Long expiresIn;
    Long refreshExpiresIn;
    String scope;
}
