package com.project.approval.dto;

import lombok.Data;

@Data
public class PositionsDTO {

    private String positionCd;  // PK
    private String positionName;
    private int levelNo;

}
