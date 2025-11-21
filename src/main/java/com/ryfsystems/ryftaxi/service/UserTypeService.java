package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.model.UserType;

import java.util.List;

public interface UserTypeService {

    UserType findById(Long id);

    List<UserType> getAvailableUserTypes();
    List<UserType> getByIdIn(List<Long> ids);
}
