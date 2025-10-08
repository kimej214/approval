package com.project.approval.controller;

import com.project.approval.dto.ApprovalListDTO;
import com.project.approval.dto.UserWithPositionDTO;
import com.project.approval.service.ApprovalServiceInter;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {
    private final ApprovalServiceInter approvalService;

    public ApprovalController(ApprovalServiceInter approvalService) {
        this.approvalService = approvalService;
    }

    // 결재 목록 조회 API (페이징)
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getApprovalListPaged(
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UserWithPositionDTO user = (UserWithPositionDTO) session.getAttribute("user");

        String userId = null;
        Integer levelNo = null;

        if (user != null) {
            userId = user.getUserId();
            levelNo = user.getLevelNo();
        }

        System.out.println("✅ userId from session = " + userId);
        System.out.println("✅ levelNo from session = " + levelNo);
        System.out.println("SESSION ID: " + session.getId());
        System.out.println("SESSION ATTRIBUTES: " + session.getAttributeNames());

        Map<String, Object> response = approvalService.getApprovalListPaged(page, size, userId, levelNo);
        return ResponseEntity.ok(response);
    }


    // ✅ 단일 결재 상세조회  (선택 시 표시)
    @GetMapping("/{num}")
    public ResponseEntity<?> getApprovalDetail(@PathVariable Long num, HttpSession session) {
        ApprovalListDTO dto = approvalService.getApprovalDetail(num);

        // ✅ 세션에서 user 객체로 가져오기
        UserWithPositionDTO user = (UserWithPositionDTO) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인 정보가 없습니다.");
        }

        String loginUserId = user.getUserId(); // ✅ 여기서 userId 추출

        if ("TMP".equals(dto.getStatusCode()) && !dto.getWriterId().equals(loginUserId)) {
            return ResponseEntity.status(403).body("임시저장 문서는 작성자만 볼 수 있습니다.");
        }

        return ResponseEntity.ok(dto);
    }

    // ✅ 작성자의 임시저장 문서 조회
    @GetMapping("/draft/{writerId}")
    public ResponseEntity<?> getDraftByWriter(@PathVariable String writerId) {
        ApprovalListDTO draft = approvalService.getDraftByWriter(writerId);
        if (draft != null) {
            return ResponseEntity.ok(draft);
        } else {
            return ResponseEntity.noContent().build(); // 204
        }
    }


    // ✅ 번호 조회 (작성 시 nextNum 미리 표시)
    @GetMapping("/nextNum")
    public Map<String, Object> getNextNum() {
        Long nextNum = approvalService.getNextNum(); // SELECT MAX(num) + 1
        Map<String, Object> res = new HashMap<>();
        res.put("nextNum", nextNum);
        return res;
    }

    // ✅ 등록 또는 임시저장 (TMP or PND)
    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> insertApproval(@RequestBody ApprovalListDTO dto) {
        // 기본값 TMP (임시저장)
        if (dto.getStatusCode() == null || dto.getStatusCode().isEmpty()) {
            dto.setStatusCode("TMP");
        }

        ApprovalListDTO saved = approvalService.insertApproval(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("success", saved.getNum() != null);
        result.put("num", saved.getNum());
        result.put("statusCode", saved.getStatusCode());
        return ResponseEntity.ok(result);
    }

    // ✅ 상태 변경 (결재 요청, 승인, 반려, 완료 등)
    @PutMapping("/{num}/status/{statusCode}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable("num") Long num,
            @PathVariable("statusCode") String statusCode
    ) {
        int updated = approvalService.updateStatus(num, statusCode);
        Map<String, Object> result = new HashMap<>();
        result.put("success", updated > 0);
        result.put("num", num);
        result.put("statusCode", statusCode);
        return ResponseEntity.ok(result);
    }

    //권한
    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleApprovals(HttpSession session) {
        UserWithPositionDTO user = (UserWithPositionDTO) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("세션 없음");

        String userId = user.getUserId();
        String positionCd = user.getPositionCd();
        List<ApprovalListDTO> list = approvalService.getVisibleApprovals(userId, positionCd);
        return ResponseEntity.ok(list);
    }
}
