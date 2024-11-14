package com.iecube.ota.model.production_member.service;

import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.production_member.entity.ProductionMember;

import java.util.List;

public interface ProductionMemberService {
    List<ProductionMember> findAll();

    List<ProductionMember> addProductionMember(List<ProductionMember> productionMemberList, CurrentUserDto currentUserDto);

    List<ProductionMember> removeProductionMember(ProductionMember productionMember, CurrentUserDto currentUserDto);

}
