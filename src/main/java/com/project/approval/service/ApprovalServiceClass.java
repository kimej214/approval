package com.project.approval.service;

import com.project.approval.dto.ApprovalListDTO;
import com.project.approval.repository.ApprovalMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalServiceClass implements ApprovalServiceInter {

    private final ApprovalMapper approvalMapper;

    public ApprovalServiceClass(ApprovalMapper approvalMapper){
        this.approvalMapper = approvalMapper;
    }

    @Override
    public Map<String, Object> getApprovalListPaged(int page, int size, String userId, Integer levelNo) {
        int offset = (page - 1) * size;

        Map<String, Object> params = new HashMap<>();
        params.put("limit", size);
        params.put("offset", offset);
        params.put("userId", userId);
        params.put("levelNo", levelNo);

        // ✅ 사원(levelNo == 1)은 본인 관련 문서만 조회
        List<ApprovalListDTO> approvals;
        int totalCount;

        if (levelNo != null && levelNo == 1) {
            approvals = approvalMapper.findApprovalsPagedByRole(params);
            totalCount = approvalMapper.countApprovalsByRole(params);
        } else {
            approvals = approvalMapper.findApprovalsPaged(params);
            totalCount = approvalMapper.countApprovals(); // 전체 카운트
        }

        Map<String, Object> result = new HashMap<>();
        result.put("approvals", approvals);
        result.put("totalCount", totalCount);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public ApprovalListDTO getApprovalDetail(Long num) {
        return approvalMapper.findApprovalByNum(num);
    }

    @Override
    public Long getNextNum() {
        Long maxNum = approvalMapper.findMaxNum(); // Mapper 호출
        return (maxNum != null ? maxNum + 1 : 1L);
    }

    @Override
    public ApprovalListDTO getDraftByWriter(String writerId) {
        return approvalMapper.findDraftByWriter(writerId);
    }


    // ✅ 결재 등록 (insert)
    @Override
    public ApprovalListDTO insertApproval(ApprovalListDTO dto) {
        approvalMapper.insertApproval(dto);
        return dto;
    }

    @Override
    public int updateStatus(Long num, String statusCode) {
        // 현재 문서 상태 조회
        ApprovalListDTO current = approvalMapper.findApprovalByNum(num);
        String nextStatus = statusCode;

        // 중간결재자가 승인한 경우 → 결재중(APR)
        if ("PND".equals(current.getStatusCode()) && "APR".equals(statusCode)) {
            nextStatus = "APR"; // 중간결재 중
        }
        // 최종결재자가 승인한 경우 → 완료(CMP)
        else if ("APR".equals(current.getStatusCode()) && "APR".equals(statusCode)) {
            nextStatus = "CMP"; // 결재완료
        }
        // 반려는 그대로
        else if ("REJ".equals(statusCode)) {
            nextStatus = "REJ";
        }

        return approvalMapper.updateStatus(num, nextStatus);
    }

    public List<ApprovalListDTO> getVisibleApprovals(String userId, String positionCd) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("positionCd", positionCd);
        return approvalMapper.findVisibleApprovals(params);
    }
}
