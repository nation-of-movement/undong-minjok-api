package com.undongminjok.api.point.repository;

import com.undongminjok.api.point.domain.Point;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.PointDetailDTO;
import com.undongminjok.api.point.dto.response.PointDetailResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

  /* 전체 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
          p.id as pointId,
           t.name as templateName,
           p.status as pointStatus,
           p.amount,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    LEFT JOIN p.template t
    WHERE u.userId = :userId
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findMyPointAll(
      @Param("userId") Long userId
  );

  /* POINT 조회 페이지 - 조건 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
          p.id as pointId,
           t.name as templateName,
           p.status as pointStatus,
           p.amount,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    LEFT JOIN p.template t
    WHERE u.userId = :userId
      AND (:pointStatus IS NULL OR p.status = :pointStatus)
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findPointByPointStatus (
      @Param("userId") Long userId,
      @Param("pointStatus") PointStatus pointStatus
  );

  /* 포인트 상세 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDetailDTO(
           p.id as pointId,
           t.name as templateName,
           p.status as pointStatus,
           t.price,
           p.amount,
           p.method as paymentMethod,
           p.bank,
           p.accountNumber,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    LEFT JOIN p.template t
    WHERE u.userId = :userId
      AND p.id = :pointId
""")
  PointDetailDTO findMyPointByPointId(
      @Param("userId") Long userId,
      @Param("pointId") Long pointId);

  /* MY POINT 조회*/
  @Query("""
    SELECT 
          SUM(p.amount) as totalPoint
    FROM Point p
    JOIN p.user u
    LEFT JOIN p.template t
    WHERE u.userId = :userId
""")
  Integer findTotalMyPoint(
      @Param("userId") Long userId
  );

  /* SELLING POINT 조회*/
  @Query("""
    SELECT 
          SUM(p.amount) as totalPoint
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND p.status IN ('EARN')
""")
  Integer findTotalSellingPoint(
      @Param("userId") Long userId
  );

}
