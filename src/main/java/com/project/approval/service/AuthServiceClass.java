package com.project.approval.service;

import com.project.approval.dto.UserWithPositionDTO;
import com.project.approval.repository.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceClass implements AuthServiceInter{

    private final UserMapper userMapper;

    public AuthServiceClass(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserWithPositionDTO login(String userName, String password) {
        // ✅ 입력값 검증 (보안용)
        if (userName == null || userName.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("아이디와 비밀번호는 필수입니다.");
        }

        try {
            return userMapper.findByUsernameAndPassword(userName, password);
        } catch (Exception e) {
            throw new RuntimeException("로그인 처리 중 오류 발생", e);
        }
    }

}
