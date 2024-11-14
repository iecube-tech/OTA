package com.iecube.ota.model.admin.service;

import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.admin.entity.AdminMember;
import com.iecube.ota.model.admin.entity.AdminMemberQo;

import java.util.List;

public interface AdminService {

    List<AdminMember> addMember(List<AdminMemberQo> adminMemberQoList, CurrentUserDto currentUserDto);

    List<AdminMember> delMember(AdminMemberQo adminMemberQo, CurrentUserDto currentUserDto);

    List<AdminMember> getMembers();

    Boolean isMember(CurrentUserDto currentUserDto);

}
