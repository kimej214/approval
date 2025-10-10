package com.project.approval.service;

import com.project.approval.dto.ApprovalListDTO;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface ApprovalServiceInter {

    List<ApprovalListDTO> findApprovalsByRole(String userId, Integer levelNo, int start, int pageSize);

    ApprovalListDTO getApprovalDetail(Long num);

    Long getNextNum(); // 가장 큰 번호 + 1 조회용

    // 등록 (insert)
    ApprovalListDTO insertApproval(ApprovalListDTO dto);

    int updateStatus(Long num, String statusCode, String approverId, HttpSession session);

    ApprovalListDTO getDraftByWriter(String writerId);

    String getNextStatus(String currentStatus, String requestStatus, int approverLevel);

    int submitDraftToPending(HttpSession session);
}