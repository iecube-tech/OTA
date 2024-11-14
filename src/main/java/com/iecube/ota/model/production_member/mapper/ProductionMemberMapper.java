package com.iecube.ota.model.production_member.mapper;

import com.iecube.ota.model.production_member.entity.ProductionMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductionMemberMapper {
    Integer insertProductionMember(ProductionMember productionMember);

    Integer deleteProductionMember(ProductionMember productionMember);

    List<ProductionMember> findAllProductionMember();

    ProductionMember getProductionMember(String unionId);
}
