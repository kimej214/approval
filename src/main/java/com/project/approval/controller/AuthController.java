package com.project.approval.controller;

import com.project.approval.dto.UserDTO;
import com.project.approval.dto.UserWithPositionDTO;
import com.project.approval.service.AuthServiceInter;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceInter authService;

    public AuthController(AuthServiceInter authService) {
        this.authService = authService;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO requestBody, HttpSession session) {
        try {
            // 로그인 시 UserWithPositionDTO를 반환하도록 변경
            UserWithPositionDTO user = authService.login(requestBody.getUserName(), requestBody.getPassword());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("아이디 또는 비밀번호 불일치");
            }

            // ✅ 세션에 사용자 저장 (직급명 포함)
            session.setAttribute("user", user);
            System.out.println("🟢 로그인 성공, 세션ID=" + session.getId());

            // ✅ 프론트로 직급 정보 포함된 JSON 반환
            return ResponseEntity.ok(user);

        } catch (IllegalArgumentException e) {
            // ✅ 입력값 잘못된 경우 → 400 응답
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // ✅ 내부 오류 → 500 응답
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }

    // 세션 확인
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        System.out.println("현재 세션 ID: " + session.getId());
        System.out.println("세션 사용자: " + session.getAttribute("user"));
        UserWithPositionDTO user = (UserWithPositionDTO) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션 없음");
        }
        return ResponseEntity.ok(user);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }
}

