package com.project.approval.service;

import com.project.approval.dto.UserDTO;
import com.project.approval.dto.UserWithPositionDTO;

import java.util.List;

public interface UserServiceInter {
    UserDTO getUser(Long id);
    List<UserDTO> getAllUsers();

    UserWithPositionDTO getUserWithPosition(Long id);
    List<UserWithPositionDTO> getAllUsersWithPosition();

}
