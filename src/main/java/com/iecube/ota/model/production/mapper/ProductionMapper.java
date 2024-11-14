package com.iecube.ota.model.production.mapper;

import com.iecube.ota.model.production.entity.Production;
import com.iecube.ota.model.production.entity.ProductionExamine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductionMapper {

    Integer insertProduction(Production production);

    Integer updateProduction(Production production);

    Production getProduction(Long productId);

    Integer insertProductionExamine(ProductionExamine productionExamine);

    Integer updateProductionExamine(ProductionExamine productionExamine);

    ProductionExamine getByExamineMessageId(String examineMessageId);

    ProductionExamine getById(Long id);
}
