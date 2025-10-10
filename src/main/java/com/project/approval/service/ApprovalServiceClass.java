package com.project.approval.service;

import com.project.approval.dto.ApprovalListDTO;
import com.project.approval.repository.ApprovalMapper;
import jakarta.servlet.http.HttpSession;
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
    // 직급별 문서 조회
    public List<ApprovalListDTO> findApprovalsByRole(String userId, Integer levelNo, int start, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("levelNo", levelNo);
        params.put("start", start);
        params.put("pageSize", pageSize);

        return approvalMapper.findApprovalsPagedByRole(params);
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


    // 결재 등록 (insert)
    @Override
    public ApprovalListDTO insertApproval(ApprovalListDTO dto) {
        approvalMapper.insertApproval(dto);
        return dto;
    }

    @Override
    public int updateStatus(Long num, String statusCode, String approverId, HttpSession session) {
        // 현재 문서 상태 조회
        ApprovalListDTO current = approvalMapper.findApprovalByNum(num);
        String nextStatus = current.getStatusCode();

        // levelNo 안전 변환
        Object levelObj = session.getAttribute("levelNo");
        int levelNo = 0;
        if (levelObj instanceof Integer) levelNo = (Integer) levelObj;
        else if (levelObj instanceof Long) levelNo = ((Long) levelObj).intValue();
        else if (levelObj instanceof String) levelNo = Integer.parseInt((String) levelObj);

        // 결재요청(=APR) 처리
        if ("APR".equals(statusCode)) {
            // 1~2단계 (사원, 대리): TMP 또는 REJ → PND
            if ((levelNo == 1 || levelNo == 2)
                    && ("TMP".equals(current.getStatusCode()) || "REJ".equals(current.getStatusCode()))) {
                nextStatus = "PND";
                approverId = null; // 과장 결재 전이라 결재자 없음
            }
            // 3단계 (과장): PND → APR
            else if (levelNo == 3 && "PND".equals(current.getStatusCode())) {
                nextStatus = "APR";
            }
            // 4단계 (부장): APR → CMP
            else if (levelNo == 4 && "APR".equals(current.getStatusCode())) {
                nextStatus = "CMP";
            }
        }
        // 반려
        else if ("REJ".equals(statusCode) && levelNo >= 3) {
            nextStatus = "REJ";
        }

        return approvalMapper.updateStatus(num, nextStatus, approverId);
    }

    @Override
    public String getNextStatus(String currentStatus, String requestStatus, int approverLevel) {
        String nextStatus = requestStatus;

        // 사원(1), 대리(2) → 중간결재자
        // 과장(3), 부장(4) 이상 → 최종결재자
        boolean isFinalApprover = approverLevel >= 3;

        if ("PND".equals(currentStatus) && "APR".equals(requestStatus)) {
            if (isFinalApprover) {
                nextStatus = "CMP"; // 최종결재자는 완료
            } else {
                nextStatus = "APR"; // 중간결재자는 결재중
            }
        } else if ("APR".equals(currentStatus) && "APR".equals(requestStatus)) {
            nextStatus = "CMP"; // 이미 중간결재중이던 문서 → 최종결재 완료
        } else if ("REJ".equals(requestStatus)) {
            nextStatus = "REJ"; // 반려
        }

        return nextStatus;
    }

    @Override
    public int submitDraftToPending(HttpSession session) {
        String writerId = (String) session.getAttribute("userId");
        if (writerId == null) return 0;

        // TMP → PND 상태 업데이트
        return approvalMapper.updateTempToPending(writerId);
    }
}
