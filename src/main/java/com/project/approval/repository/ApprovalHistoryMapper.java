package com.project.approval.repository;

import com.project.approval.dto.ApprovalHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalHistoryMapper {
    void insertInitialHistory(@Param("num") Long num, @Param("writerId") String writerId);
    void insertStepHistory(@Param("num") Long num, @Param("procId") String procId, @Param("statusCode") String statusCode);
    List<ApprovalHistoryDTO> findHistoryByApprovalNum(Long approvalNum);
}
