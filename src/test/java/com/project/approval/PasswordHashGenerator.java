package com.project.approval;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// PasswordHashGenerator
// 비밀번호 암호화 테스트용 (DB 해시 생성 전용)
// src/test/java에서만 사용됨. 배포 시 실행되지 않음.

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("pg (a1234): " + encoder.encode("a1234"));
        System.out.println("aa (s1234): " + encoder.encode("s1234"));
        System.out.println("pl (q1234): " + encoder.encode("q1234"));
        System.out.println("pm (w1234): " + encoder.encode("w1234"));
    }
}