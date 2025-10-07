package com.project.approval.repository;

import com.project.approval.dto.PositionsDTO;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PositionsMapper {
    PositionsDTO findPositionByCd(String positionCd);
    List<PositionsDTO> findAllPositions();
}
