package com.project.approval.service;


import com.project.approval.dto.UserDTO;
import com.project.approval.dto.UserWithPositionDTO;
import org.springframework.stereotype.Service;
import com.project.approval.repository.UserMapper;

import java.util.List;

@Service
public class UserServiceClass implements UserServiceInter {

    private final UserMapper userMapper;

    public UserServiceClass(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO getUser(Long id) {
        return userMapper.findUserById(id);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.findAllUsers();
    }

    @Override
    public UserWithPositionDTO getUserWithPosition(Long id) {
        return userMapper.findUserWithPosition(id);
    }

    @Override
    public List<UserWithPositionDTO> getAllUsersWithPosition() {
        return userMapper.findAllUsersWithPosition();
    }

    // 로그인용 추가 (직급 정보 포함)
    public UserWithPositionDTO login(String userName, String password) {
        return userMapper.findByUsernameAndPassword(userName, password);
    }
}
