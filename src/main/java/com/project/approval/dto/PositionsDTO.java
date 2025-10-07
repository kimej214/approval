package com.project.approval.dto;

import lombok.Data;

@Data
public class PositionsDTO {

    private String position_cd;  // PK
    private String position_name;
    private int level_no;

}
