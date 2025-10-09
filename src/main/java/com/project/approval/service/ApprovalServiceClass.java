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
}
