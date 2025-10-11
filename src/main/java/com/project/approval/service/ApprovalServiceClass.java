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
    public int countApprovalsByRole(String userId, Integer levelNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("levelNo", levelNo);
        return approvalMapper.countApprovalsByRole(params);
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

        // 결재요청 처리 (사원~부장 공통)
        if ("APR".equals(statusCode)) {

            // 사원·대리·과장·부장: TMP 또는 REJ → PND (결재요청)
            if ((levelNo >= 1 && levelNo <= 3)
                    && ("TMP".equals(current.getStatusCode()) || "REJ".equals(current.getStatusCode()))) {
                nextStatus = "PND";
                approverId = null; // 결재자 미지정
            }

            // 과장: 결재대기 or 반려 → 결재중
            else if (levelNo == 3 &&
                    ("PND".equals(current.getStatusCode()) || "REJ".equals(current.getStatusCode()))) {
                nextStatus = "APR";
                approverId = loginUser;
            }

            // 부장: 결재대기(PND) → 결재중(APR)
            else if (levelNo == 4 && "PND".equals(current.getStatusCode())) {
                nextStatus = "APR";
                approverId = loginUser;
            }

            // 부장: 결재중(APR) → 완료(CMP)
            else if (levelNo == 4 && "APR".equals(current.getStatusCode())) {
                nextStatus = "CMP";
                approverId = loginUser;
            }
        }

        // 반려 처리
        else if ("REJ".equals(statusCode)) {

            // 과장 반려 → 하위직(사원·대리) 확정 반려
            if (levelNo == 3) {
                nextStatus = "REJ";
                approverId = loginUser;
            }

            // 부장 반려 → 작성자가 재요청 가능하도록 approver_id 초기화
            else if (levelNo == 4) {
                nextStatus = "REJ";
                approverId = null;
            }
        }

        // 상태 업데이트
        int updated = approvalMapper.updateStatus(num, nextStatus, approverId);

        // 이력 기록
        if (updated > 0) {
            if (approverId == null && "PND".equals(nextStatus)) {
                // 최초 작성자 (임시저장 → 결재대기)
                approvalHistoryMapper.insertInitialHistory(num, current.getWriterId());
            } else if (approverId != null) {
                // 승인/진행 단계 (결재자 있음)
                approvalHistoryMapper.insertStepHistory(num, loginUser, nextStatus);
            } // 3 부장 반려(REJ) - approverId가 null이지만 기록 남기기
            else if (approverId == null && "REJ".equals(nextStatus)) {
                approvalHistoryMapper.insertStepHistory(num, loginUser, nextStatus);
            }
        }

        return nextStatus;
    }

    @Override
    public int submitDraftToPending(HttpSession session) {
        String writerId = (String) session.getAttribute("userId");
        if (writerId == null) return 0;

        // 현재 임시저장 문서 가져오기
        ApprovalListDTO draft = approvalMapper.findDraftByWriter(writerId);
        if (draft == null) return 0;

        // num 기준으로 상태 갱신
        int updated = approvalMapper.updateTempToPending(draft.getNum(), writerId);

        // 결재이력 추가
        if (updated > 0) {
            approvalHistoryMapper.insertInitialHistory(draft.getNum(), writerId);
        }

        return updated;
    }

    @Override
    public List<ApprovalHistoryDTO> getApprovalHistory(Long num) {
        List<ApprovalHistoryDTO> list = approvalHistoryMapper.findHistoryByApprovalNum(num);
        return list.stream()
                .filter(h -> !"TMP".equals(h.getStatusCode())) // TMP 제거
                .toList();
    }

}
