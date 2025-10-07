package com.project.approval.service;

import com.project.approval.dto.ApprovalListDTO;

import java.util.Map;

public interface ApprovalServiceInter {

    // ✅ 페이징 목록
    Map<String, Object> getApprovalListPaged(int page, int size);

    ApprovalListDTO getApprovalDetail(Long num);

    Long getNextNum(); // ✅ 가장 큰 번호 + 1 조회용

    // ✅ 등록 (insert)
    ApprovalListDTO insertApproval(ApprovalListDTO dto);

    int updateStatus(Long num, String statusCode);

    ApprovalListDTO getDraftByWriter(String writerId);
}