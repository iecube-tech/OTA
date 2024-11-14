package com.iecube.ota.model.admin.service.Impl;

import com.iecube.ota.exception.DeleteException;
import com.iecube.ota.exception.InsertException;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.admin.entity.AdminMember;
import com.iecube.ota.model.admin.entity.AdminMemberQo;
import com.iecube.ota.model.admin.mapper.AdminMapper;
import com.iecube.ota.model.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public List<AdminMember> addMember(List<AdminMemberQo> adminMemberQoList, CurrentUserDto currentUserDto) {
        List<AdminMemberQo> filedList = new ArrayList<>();
        for (AdminMemberQo adminMemberQo : adminMemberQoList) {
            if(adminMapper.getAdmin(adminMemberQo.getUnionId()) != null) {
                filedList.add(adminMemberQo);
                continue;
            }
            AdminMember adminMember = new AdminMember();
            adminMember.setName(adminMemberQo.getName());
            adminMember.setUnionId(adminMemberQo.getUnionId());
            adminMember.setAvatar(adminMemberQo.getAvatar());
            adminMember.setCreatorName(currentUserDto.getUserInfoDto().getName());
            adminMember.setCreatorUnionId(currentUserDto.getUserInfoDto().getUnionId());
            adminMember.setCreatorAvatar(currentUserDto.getUserInfoDto().getAvatarThumb());
            adminMember.setCreateTime(new Date());
            Integer res =adminMapper.insertAdmin(adminMember);
            if(res != 1){
                filedList.add(adminMemberQo);
            }
        }
        if(!filedList.isEmpty()){
            StringBuilder names = new StringBuilder();
            for(AdminMemberQo adminMemberQo : filedList){
                names.append(adminMemberQo.getName()).append(",");
            }
            throw new InsertException("以下人员添加失败:"+names);
        }
        // todo 发送消息通知
        return adminMapper.allAdmin();
    }

    @Override
    public List<AdminMember> delMember(AdminMemberQo adminMemberQo, CurrentUserDto currentUserDto) {
        AdminMember adminMember = adminMapper.getAdmin(adminMemberQo.getUnionId());
        if(adminMember == null){
            throw new DeleteException("数据不存在，删除失败");
        }
        if(!adminMember.getCreatorUnionId().equals(currentUserDto.getUserInfoDto().getUnionId())){
            throw new DeleteException("非本人添加的管理员，无权删除");
        }
        Integer res = adminMapper.deleteAdmin(adminMember.getId());
        if(res != 1){
            throw new DeleteException("删除数据异常");
        }
        return adminMapper.allAdmin();
    }

    @Override
    public List<AdminMember> getMembers() {
        return adminMapper.allAdmin();
    }

    @Override
    public Boolean isMember(CurrentUserDto currentUserDto) {
        AdminMember adminMember = adminMapper.getAdmin(currentUserDto.getUserInfoDto().getUnionId());
        return adminMember != null;
    }
}
