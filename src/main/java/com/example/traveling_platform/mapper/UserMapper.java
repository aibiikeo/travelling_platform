package com.example.traveling_platform.mapper;

import com.example.traveling_platform.dto.UserDto;
import com.example.traveling_platform.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity userDtoToUser(UserDto dto);
    UserDto userToUserDto(UserEntity user);
}
