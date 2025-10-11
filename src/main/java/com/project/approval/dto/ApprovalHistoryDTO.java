package com.project.approval.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ApprovalHistoryDTO {

    private Long hisNum;
    private Long approvalNum;
    private String procId;
    private String positionCd;
    private String statusCode; // 실제 코드값 (PND, REJ 등)
    private String statusName; // 상태 한글명 (결재대기, 반려 등)
    private Date hisRegDate;

}
