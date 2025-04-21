package com.epam.resource.mapper;

import com.epam.resource.dto.ResourceDto;
import com.epam.resource.repository.domain.Resource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    ResourceDto toResourceDto(Resource resource);

}
