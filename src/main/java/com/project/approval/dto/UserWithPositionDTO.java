package com.project.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithPositionDTO {
    private String id;// emp_no
    private String username;
    private String userId;
    private String password;
    private String positionCd; // position_cd
    private String positionName;
    private int levelNo;
}