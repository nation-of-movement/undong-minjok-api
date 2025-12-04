package com.undongminjok.api.point.repository;

import com.undongminjok.api.point.domain.PageType;
import com.undongminjok.api.point.domain.Point;
import com.undongminjok.api.point.domain.PointType;
import com.undongminjok.api.point.dto.PointDTO;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {


  // AND (:pointType IS NULL OR p.type = :pointType)
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
           p.id as pointId,
           t.id as templateId,
           t.name as templateName,
           p.type as pointType,
           p.method as paymentMethod,
           u.amount as totalPoint
        )
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND (
            (:pageType = 'MY' AND p.type IS NULL AND p.type IN ('RECHARGE', 'PURCHASE', 'REFUND')) OR
            (:pageType = 'MY' AND p.type IS NOT NULL AND p.type = :pointType) OR
            (:pageType = 'SELLING' AND p.type IS NULL AND p.type IN ('SALE', 'WITHDRAW')) OR
            (:pageType = 'SELLING' AND p.type IS NOT NULL AND p.type = :pointType)
      )
""")
  List<PointDTO> findPointDTOByUserIdAndPointType(
      @Param("userId") Long userId,
      @Param("pointType") PointType pointType,
      @Param("pageType") PageType pageType
  );
}
