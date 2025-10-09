package com.project.approval.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String id;  // emp_no
    private String userName;   // user_id
    private String password;   // user_pw
    private String positionCd; // position_cd
}
