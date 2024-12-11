package com.nc.Xmlxsdvalidate.dao;

import com.nc.Xmlxsdvalidate.dto.UserDto;

import java.util.List;

public interface UserDao {
    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto save(UserDto userDto);

    void deleteById(Long id);
}

