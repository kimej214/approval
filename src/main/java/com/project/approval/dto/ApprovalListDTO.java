package com.project.approval.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ApprovalListDTO {
    private Long num;          // ✅ 글번호 (AUTO_INCREMENT)
    private String writerId;   // 작성자 ID
    private String title;      // 제목
    private String content;    // 내용
    private LocalDate regDate; // 등록일 (LocalDate로 변경)
    private LocalDate apprDate; // 결재일
    private String approverId; // 결재자 ID
    private String statusCode; // 상태 코드
}
