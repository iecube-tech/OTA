package com.iecube.ota.model.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AdminMember {
    private Long id;
    private String unionId;
    private String name;
    private String avatar;
    private Date createTime;
    private String creatorName;
    private String creatorUnionId;
    private String creatorAvatar;
}
