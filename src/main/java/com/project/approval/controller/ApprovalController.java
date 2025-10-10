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
    public ResponseEntity<?> getApprovalList(
            @RequestParam(defaultValue = "1") int page,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        Integer levelNo = (Integer) session.getAttribute("levelNo");

        int pageSize = 10;
        int start = (page - 1) * pageSize;

        System.out.println("✅ userId from session = " + userId);
        System.out.println("✅ levelNo from session = " + levelNo);
        System.out.println("SESSION ID: " + session.getId());
        System.out.println("SESSION ATTRIBUTES: " + session.getAttributeNames());

        List<ApprovalListDTO> approvals = approvalService.findApprovalsByRole(userId, levelNo, start, pageSize);
        return ResponseEntity.ok(approvals);
    }

    // 단일 결재 상세조회  (선택 시 표시)
    @GetMapping("/{num}")
    public ResponseEntity<?> getApprovalDetail(@PathVariable Long num, HttpSession session) {
        ApprovalListDTO dto = approvalService.getApprovalDetail(num);

        // 세션에서 user 객체로 가져오기
        UserWithPositionDTO user = (UserWithPositionDTO) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인 정보가 없습니다.");
        }

        String loginUserId = user.getUserId(); // 여기서 userId 추출

        if ("TMP".equals(dto.getStatusCode()) && !dto.getWriterId().equals(loginUserId)) {
            return ResponseEntity.status(403).body("임시저장 문서는 작성자만 볼 수 있습니다.");
        }

        return ResponseEntity.ok(dto);
    }

    // 작성자의 임시저장 문서 조회
    @GetMapping("/draft/{writerId}")
    public ResponseEntity<?> getDraftByWriter(@PathVariable String writerId) {
        ApprovalListDTO draft = approvalService.getDraftByWriter(writerId);
        if (draft != null) {
            return ResponseEntity.ok(draft);
        } else {
            return ResponseEntity.noContent().build(); // 204
        }
    }

    // 번호 조회 (작성 시 nextNum 미리 표시)
    @GetMapping("/nextNum")
    public Map<String, Object> getNextNum() {
        Long nextNum = approvalService.getNextNum(); // SELECT MAX(num) + 1
        Map<String, Object> res = new HashMap<>();
        res.put("nextNum", nextNum);
        return res;
    }

    // 등록 또는 임시저장 (TMP or PND)
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

    // 상태 변경 (결재 요청, 승인, 반려, 완료 등)
    @PutMapping("/{num}/status/{statusCode}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable("num") Long num,
            @PathVariable("statusCode") String statusCode,
            HttpSession session
    ) {
        // 세션에서 로그인 사용자 가져오기
        UserWithPositionDTO user = (UserWithPositionDTO) session.getAttribute("user");
        String approverId = user != null ? user.getUserId() : null;
        int approverLevel = user != null ? user.getLevelNo() : 0;

        ApprovalListDTO current = approvalService.getApprovalDetail(num);

        // 직급 레벨 고려해서 다음 상태 계산
        String nextStatus = approvalService.getNextStatus(current.getStatusCode(), statusCode, approverLevel);

        // DB 업데이트
        int updated = approvalService.updateStatus(num, nextStatus, approverId, session);

        // 응답 반환
        Map<String, Object> result = new HashMap<>();
        result.put("success", updated > 0);
        result.put("num", num);
        result.put("statusCode", statusCode);
        result.put("approverId", approverId);
        result.put("appliedStatus", nextStatus);

        return ResponseEntity.ok(result);
    }

    // 임시저장 문서를 결재요청으로 전환
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitApproval(HttpSession session) {
        String writerId = (String) session.getAttribute("userId");
        if (writerId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인 정보가 없습니다."));
        }

        ApprovalListDTO draft = approvalService.getDraftByWriter(writerId);
        if (draft == null) {
            return ResponseEntity.ok(Map.of("success", false, "message", "임시저장 문서가 없습니다."));
        }

        int updated = approvalService.submitDraftToPending(session);

        Map<String, Object> result = new HashMap<>();
        result.put("success", updated > 0);
        result.put("num", draft.getNum()); // 이동용 번호
        result.put("message", updated > 0 ? "결재요청되었습니다." : "업데이트 실패");

        return ResponseEntity.ok(result);
    }
}
