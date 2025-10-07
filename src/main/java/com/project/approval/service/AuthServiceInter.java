package com.project.approval.service;

import com.project.approval.dto.UserWithPositionDTO;

public interface AuthServiceInter {
    UserWithPositionDTO login(String userName, String password);
}
