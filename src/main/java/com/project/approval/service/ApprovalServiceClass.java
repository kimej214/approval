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
    public Map<String, Object> getApprovalListPaged(int page, int size) {
        int offset = (page - 1) * size;
        List<ApprovalListDTO> list = approvalMapper.findApprovalsPaged(size, offset);
        int totalCount = approvalMapper.countApprovals();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        result.put("totalCount", totalCount);
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
        return approvalMapper.updateStatus(num, statusCode);
    }
}
