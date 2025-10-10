package com.project.approval.service;

import com.project.approval.dto.ApprovalHistoryDTO;
import com.project.approval.dto.ApprovalListDTO;
import com.project.approval.repository.ApprovalHistoryMapper;
import com.project.approval.repository.ApprovalMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalServiceClass implements ApprovalServiceInter {

    private final ApprovalMapper approvalMapper;
    private final ApprovalHistoryMapper approvalHistoryMapper;

    public ApprovalServiceClass(ApprovalMapper approvalMapper,  ApprovalHistoryMapper approvalHistoryMapper){
        this.approvalMapper = approvalMapper;
        this.approvalHistoryMapper = approvalHistoryMapper;
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
    public String updateStatus(Long num, String statusCode, String approverId, HttpSession session) {
        // 현재 문서 상태 조회
        ApprovalListDTO current = approvalMapper.findApprovalByNum(num);
        String nextStatus = current.getStatusCode();

        // 로그인 정보
        String loginUser = (String) session.getAttribute("userId");
        Object levelObj = session.getAttribute("levelNo");
        int levelNo = 0;
        if (levelObj instanceof Integer) levelNo = (Integer) levelObj;
        else if (levelObj instanceof Long) levelNo = ((Long) levelObj).intValue();
        else if (levelObj instanceof String) levelNo = Integer.parseInt((String) levelObj);

        // 결재요청 처리
        if ("APR".equals(statusCode)) {

            // 사원·대리: 임시저장 or 반려 → 결재대기
            if ((levelNo == 1 || levelNo == 2)
                    && ("TMP".equals(current.getStatusCode()) || "REJ".equals(current.getStatusCode()))) {
                nextStatus = "PND";
                approverId = null; // 아직 결재자 없음
            }

            // 과장: 결재대기(PND) or 반려(REJ) 문서 → 결재중(APR)
            else if (levelNo == 3 && ("PND".equals(current.getStatusCode()) || "REJ".equals(current.getStatusCode()))) {
                nextStatus = "APR";
                approverId = loginUser;
            }

            // 부장: 결재중(APR) 문서 → 완료(CMP)
            else if (levelNo == 4 && "APR".equals(current.getStatusCode())) {
                nextStatus = "CMP";
                approverId = loginUser;
            }
        }

        // 반려 처리
        else if ("REJ".equals(statusCode) && levelNo >= 3) {
            nextStatus = "REJ";
            approverId = loginUser; // 현재 결재자 기록 남김
        }

        // 상태 업데이트
        int updated = approvalMapper.updateStatus(num, nextStatus, approverId);

        // 이력 기록
        if (updated > 0) {
            if (approverId == null) {
                // 최초 작성자 (임시저장 → 결재대기)
                approvalHistoryMapper.insertInitialHistory(num, current.getWriterId());
            } else {
                // 결재자 단계별 기록
                approvalHistoryMapper.insertStepHistory(num, loginUser, nextStatus);
            }
        }

        return nextStatus;
    }

    @Override
    public int submitDraftToPending(HttpSession session) {
        String writerId = (String) session.getAttribute("userId");
        if (writerId == null) return 0;

        // 1️TMP → PND 상태 변경
        int updated = approvalMapper.updateTempToPending(writerId);

        // 2️변경된 문서 번호 다시 조회 (이제 PND 상태로 조회)
        if (updated > 0) {
            Long updatedNum = approvalMapper.findLatestPendingByWriter(writerId);
            if (updatedNum != null) {
                approvalHistoryMapper.insertInitialHistory(updatedNum, writerId);
            }
        }

        return updated;
    }

    @Override
    public List<ApprovalHistoryDTO> getApprovalHistory(Long num) {
        return approvalHistoryMapper.findHistoryByApprovalNum(num);
    }

}
