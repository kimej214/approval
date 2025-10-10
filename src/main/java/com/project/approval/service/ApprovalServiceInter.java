package com.project.approval.service;

import com.project.approval.dto.ApprovalHistoryDTO;
import com.project.approval.dto.ApprovalListDTO;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface ApprovalServiceInter {

    List<ApprovalListDTO> findApprovalsByRole(String userId, Integer levelNo, int start, int pageSize);

    ApprovalListDTO getApprovalDetail(Long num);

    Long getNextNum(); // 가장 큰 번호 + 1 조회용

    // 등록 (insert)
    ApprovalListDTO insertApproval(ApprovalListDTO dto);

    String updateStatus(Long num, String statusCode, String approverId, HttpSession session);

    ApprovalListDTO getDraftByWriter(String writerId);

    int submitDraftToPending(HttpSession session);

    List<ApprovalHistoryDTO> getApprovalHistory(Long num);
}