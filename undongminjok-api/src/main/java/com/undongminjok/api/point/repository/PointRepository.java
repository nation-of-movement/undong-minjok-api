package com.undongminjok.api.point.repository;

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

  /* MY POINT 조회 페이지 - 전체 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
           p.id as pointId,
           t.id as templateId,
           t.name as templateName,
           p.type as pointType,
           p.method as paymentMethod,
           u.amount as totalPoint,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND p.type IN ('RECHARGE', 'PURCHASE', 'REFUND')
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findMyPointAll(
      @Param("userId") Long userId
  );

  /* MY POINT/SELLING POINT 조회 페이지 - 조건 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
           p.id as pointId,
           t.id as templateId,
           t.name as templateName,
           p.type as pointType,
           p.method as paymentMethod,
           u.amount as totalPoint,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND (:pointType IS NULL OR p.type = :pointType)
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findPointByPointType (
      @Param("userId") Long userId,
      @Param("pointType") PointType pointType
  );

  /* SELLING POINT 조회 페이지 - 전체 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
           p.id as pointId,
           t.id as templateId,
           t.name as templateName,
           p.type as pointType,
           p.method as paymentMethod,
           u.amount as totalPoint,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND p.type IN ('SALE', 'WITHDRAW')
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findSellingPointAll(
      @Param("userId") Long userId
  );

}
