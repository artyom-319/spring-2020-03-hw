package com.etn319.web.dto.mappers;

import com.etn319.model.ServiceUser;
import com.etn319.web.dto.UserDto;

import java.util.ArrayList;

public class UserMapper {
    public static UserDto toDto(ServiceUser domainObject) {
        return UserDto.builder()
                .id(domainObject.getId())
                .name(domainObject.getName())
                .roles(new ArrayList<>(domainObject.getAuthorities()))
                .build();
    }

    public static ServiceUser toDomainObject(UserDto dto) {
        return new ServiceUser(dto.getId(), dto.getName(), dto.getRoles());
    }
}
