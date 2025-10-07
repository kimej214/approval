package com.project.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor   // ✅ 추가
public class UserWithPositionDTO {
    private String id;// emp_no
    private String username;
    private String userId;       // 새 필드 추가 가능
    private String password;
    private String positionCd; // position_cd
    private String positionName;
    private int levelNo;
}