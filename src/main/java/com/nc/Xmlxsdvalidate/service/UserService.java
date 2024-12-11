package com.nc.Xmlxsdvalidate.service;

import com.nc.Xmlxsdvalidate.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto save(UserDto userDto);

    void deleteById(Long id);
}