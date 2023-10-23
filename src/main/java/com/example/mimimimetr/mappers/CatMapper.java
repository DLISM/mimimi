package com.example.mimimimetr.mappers;

import com.example.mimimimetr.dto.CreateCatDto;
import com.example.mimimimetr.models.Cat;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CatMapper {
    CatMapper INSTANCE = Mappers.getMapper(CatMapper.class);

    Cat dtoToEntity(CreateCatDto createCatDto);

    CreateCatDto entityToDto(Cat cat);
}
