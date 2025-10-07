package com.project.approval.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ApprovalHistoryDTO {

    private int his_num;
    private int approval_num;
    private String proc_id;
    private String position_cd;
    private String status_code;
    private Date his_reg_date;

}
