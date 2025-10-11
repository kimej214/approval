package com.project.approval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

// DB에 있는 전체 유저들의 암호가 정상 저장됐나? (전체 점검용)
// 운영 DB 비밀번호 검증용 툴. 직접 DB에서 유저 목록을 불러와서, 각 유저의 user_pw(해시)와 “예상 평문”을 비교해
// 일치 / 불일치 여부를 콘솔에 출력하는 검증용

// 체커는 '내가 직접 넣은 해시값이 평문과 맞는지' 코드 내부에서만 확인. DB는 전혀 안 건드림. 완전히 수동 확인용 해시값 직접 붙여넣어야 함

@SpringBootApplication
public class PasswordMatchFromDB {

    public static void main(String[] args) {
        // Spring Boot Context 로드 (DB 연결 사용)
        ApplicationContext context = SpringApplication.run(PasswordMatchFromDB.class, args);
        DataSource dataSource = context.getBean(DataSource.class);
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // 직급별 평문 비밀번호 매핑
        Map<String, String> rawPasswords = new HashMap<>();
        rawPasswords.put("pg", "a1234");
        rawPasswords.put("aa", "s1234");
        rawPasswords.put("pl", "q1234");
        rawPasswords.put("pm", "w1234");

        System.out.println("====== DB 비밀번호 검증 시작 ======");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT user_id, position_cd, user_pw FROM approval_emp ORDER BY position_cd, user_id")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String pos = rs.getString("position_cd");
                String encodedPw = rs.getString("user_pw");

                String rawPw = rawPasswords.get(pos);
                boolean match = encoder.matches(rawPw, encodedPw);

                System.out.printf("[%s] (%s) → %s%n",
                        userId, rawPw, match ? "✅ 일치" : "❌ 불일치");
            }

        } catch (SQLException e) {
            System.err.println("DB 접근 오류: " + e.getMessage());
        }

        System.out.println("====== 검증 완료 ======");
        System.exit(0); // 프로그램 종료
    }
}