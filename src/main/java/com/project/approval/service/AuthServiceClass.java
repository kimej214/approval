package com.project.approval.service;

import com.project.approval.dto.UserWithPositionDTO;
import com.project.approval.repository.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceClass implements AuthServiceInter{

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceClass(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserWithPositionDTO login(String userName, String password) {
        // 아이디로만 사용자 조회
        UserWithPositionDTO user = userMapper.findByUsername(userName);

        // 아이디 존재 여부 확인
        if (user == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 입력 비밀번호와 DB 해시값 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("❌ 비밀번호 불일치: " + password);
            System.out.println("🧩 DB 저장 해시: " + user.getPassword());
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        // 검증 통과 → 로그인 성공
        System.out.println("✅ 비밀번호 일치! 로그인 성공");
        return user;
    }

}
