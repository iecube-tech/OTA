package com.iecube.ota.model.production_member.service.Impl;

import com.iecube.ota.exception.DeleteException;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.production_member.entity.ProductionMember;
import com.iecube.ota.model.production_member.mapper.ProductionMemberMapper;
import com.iecube.ota.model.production_member.service.ProductionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductionMemberServiceImpl implements ProductionMemberService {

    @Autowired
    private ProductionMemberMapper productionMemberMapper;


    @Override
    public List<ProductionMember> findAll() {
        return productionMemberMapper.findAllProductionMember();
    }

    @Override
    public List<ProductionMember> addProductionMember(List<ProductionMember> productionMemberList, CurrentUserDto currentUserDto) {
        for (ProductionMember productionMember : productionMemberList) {
            productionMember.setCreatorAvatar(currentUserDto.getUserInfoDto().getAvatarThumb());
            productionMember.setCreatorName(currentUserDto.getUserInfoDto().getName());
            productionMember.setCreatorUnionId(currentUserDto.getUserInfoDto().getUnionId());
            productionMember.setCreateTime(new Date());
            productionMemberMapper.insertProductionMember(productionMember);
        }
        return productionMemberMapper.findAllProductionMember();
    }

    @Override
    public List<ProductionMember> removeProductionMember(ProductionMember productionMember, CurrentUserDto currentUserDto) {
        ProductionMember existMember = productionMemberMapper.getProductionMember(productionMember.getUnionId());
        if(existMember != null) {
            productionMemberMapper.deleteProductionMember(existMember);
        }
        else {
            throw new DeleteException("未找到数据");
        }
        return productionMemberMapper.findAllProductionMember();
    }
}
