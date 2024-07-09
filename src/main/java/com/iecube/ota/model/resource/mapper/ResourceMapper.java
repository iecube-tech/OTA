package com.iecube.ota.model.resource.mapper;

import com.iecube.ota.model.resource.entity.Resource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResourceMapper {
    Integer insert(Resource resource);

    Integer delete(Long id);

    Resource getByName(String name);

    Resource getByFileName(String filename);

    Resource getById(Long id);

}
