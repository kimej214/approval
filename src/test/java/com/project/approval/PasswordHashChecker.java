package com.project.approval;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashChecker {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 🔹 새로 만든 해시 4개 (직급별)
        String pgHash = "$2a$10$ldcnKp0ppLSj94dRgcDK8uGlGkB9fye9puIEVhNW9DBrFtPulL7kK";
        String aaHash = "$2a$10$ObH7/DGCUek0Pd1vVzywIuMefiBLCXO00O/xYHjjSbPudsGxlKhfq";
        String plHash = "$2a$10$EQAhTnzmHZiodBz9xY3oKejJrR7/kIWadFukQkEOg4Uk.flru.eTy";
        String pmHash = "$2a$10$.6mYzisYpfou3tvB9.vaEu3NCQsILz52pQPtsDCJNXMxXJ9HD6Iiu";

        // 🔹 평문 비밀번호 (직급별 대응)
        String pgRaw = "a1234";
        String aaRaw = "s1234";
        String plRaw = "q1234";
        String pmRaw = "w1234";

        System.out.println("====== 비밀번호 해시 검증 ======");

        System.out.printf("pg (%s) → %s%n", pgRaw, encoder.matches(pgRaw, pgHash) ? "✅ 일치" : "❌ 불일치");
        System.out.printf("aa (%s) → %s%n", aaRaw, encoder.matches(aaRaw, aaHash) ? "✅ 일치" : "❌ 불일치");
        System.out.printf("pl (%s) → %s%n", plRaw, encoder.matches(plRaw, plHash) ? "✅ 일치" : "❌ 불일치");
        System.out.printf("pm (%s) → %s%n", pmRaw, encoder.matches(pmRaw, pmHash) ? "✅ 일치" : "❌ 불일치");

        System.out.println("====== 검증 완료 ======");
    }
}