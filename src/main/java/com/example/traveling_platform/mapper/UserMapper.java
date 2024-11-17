package com.example.traveling_platform.mapper;

import com.example.traveling_platform.dto.UserDto;
import com.example.traveling_platform.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    UserEntity userDtoToUser(UserDto dto);

    @Mapping(target = "id", ignore = true)
    UserDto userToUserDto(UserEntity user);
}
