package com.project.approval.repository;

import com.project.approval.dto.ApprovalListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApprovalMapper {

    // 페이징 목록 조회
    List<ApprovalListDTO> findApprovalsPaged(@Param("limit") int limit, @Param("offset") int offset);

    // 전체 행 개수 조회
    int countApprovals();

    // ✅ 단일 결재 상세 조회
    ApprovalListDTO findApprovalByNum(@Param("num") Long num);

    // ✅ 현재 최대 글번호 조회 (nextNum 용)
    Long findMaxNum(); // ✅ 현재 최대 글번호

    // ✅ 등록 (임시저장 또는 결재요청)
    void insertApproval(ApprovalListDTO dto);

    // ✅ 상태코드 변경 (결재요청 / 승인 / 반려 / 완료)
    int updateStatus(@Param("num") Long num,
                     @Param("statusCode") String statusCode);

    ApprovalListDTO findDraftByWriter(String writerId);

    List<ApprovalListDTO> findVisibleApprovals(Map<String, Object> params);

    List<ApprovalListDTO> findApprovalsPagedByRole(Map<String, Object> params);

    int countApprovalsByRole(Map<String, Object> params);

    List<ApprovalListDTO> findApprovalsPaged(Map<String, Object> params);
}
