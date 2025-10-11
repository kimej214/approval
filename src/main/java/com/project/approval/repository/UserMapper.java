package com.project.approval.repository;

import com.project.approval.dto.UserDTO;
import com.project.approval.dto.UserWithPositionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    UserDTO findUserById(Long id);
    List<UserDTO> findAllUsers();

    UserWithPositionDTO findUserWithPosition(Long id);
    List<UserWithPositionDTO> findAllUsersWithPosition();


    // 로그인용 (직급 포함)
    UserWithPositionDTO findByUsernameAndPassword(
            @Param("userName") String userName,
            @Param("password") String password);

    void insertUser(UserDTO dto);

    UserWithPositionDTO findByUsername(String userName);
}

