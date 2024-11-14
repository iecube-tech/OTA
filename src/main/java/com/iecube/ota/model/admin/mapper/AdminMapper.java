package com.iecube.ota.model.admin.mapper;

import com.iecube.ota.model.admin.entity.AdminMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    Integer insertAdmin(AdminMember adminMember);

    Integer deleteAdmin(Long adminId);

    List<AdminMember> allAdmin();

    AdminMember getAdmin(String unionId);

}
