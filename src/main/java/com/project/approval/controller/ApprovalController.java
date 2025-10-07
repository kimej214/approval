package com.project.approval.controller;

import com.project.approval.dto.ApprovalListDTO;
import com.project.approval.service.ApprovalServiceInter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
@CrossOrigin(origins = "http://localhost:5173")
public class ApprovalController {
    private final ApprovalServiceInter approvalService;

    public ApprovalController(ApprovalServiceInter approvalService) {
        this.approvalService = approvalService;
    }

    // 결재 목록 조회 API (페이징)
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getApprovalListPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = approvalService.getApprovalListPaged(page, size);
        return ResponseEntity.ok(response);
    }

    // ✅ 단일 결재 상세조회  (선택 시 표시)
    @GetMapping("/{num}")
    public ResponseEntity<ApprovalListDTO> getApprovalDetail(@PathVariable("num") Long num) {
        ApprovalListDTO dto = approvalService.getApprovalDetail(num);
        return ResponseEntity.ok(dto);
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
}
