package com.undongminjok.api.point.repository;

import com.undongminjok.api.point.domain.Point;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.response.PointDetailResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

  //  /* MY POINT 조회 페이지 - 전체 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
           p.id as pointId,
           t.name as templateName,
           p.status as pointStatus,
           t.price,
           u.amount as totalPoint,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND p.status IN ('RECHARGE', 'PURCHASE', 'REFUND', 'REFUND_WAIT')
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findMyPointAll(
      @Param("userId") Long userId
  );

  /* MY POINT/SELLING POINT 조회 페이지 - 조건 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
          p.id as pointId,
           t.name as templateName,
           p.status as pointStatus,
           t.price,
           u.amount as totalPoint,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND (:pointStatus IS NULL OR p.status = :pointStatus)
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findPointByPointStatus (
      @Param("userId") Long userId,
      @Param("pointStatus") String pointStatus
  );

  /* SELLING POINT 조회 페이지 - 전체 조회 */
  @Query("""
    SELECT new com.undongminjok.api.point.dto.PointDTO(
         p.id as pointId,
           t.name as templateName,
           p.status as pointStatus,
           t.price,
           u.amount as totalPoint,
           p.createdAt as createdDt
        )
     FROM Point p
     JOIN p.user u
     JOIN p.template t
    WHERE u.userId = :userId
      AND p.status IN ('SALE', 'WITHDRAW', 'WITHDRAW_WAIT')
    ORDER BY p.createdAt DESC
""")
  List<PointDTO> findSellingPointAll(
      @Param("userId") Long userId
  );

  @Query("""
    SELECT new com.undongminjok.api.point.dto.response.PointDetailResponse(
           p.id as pointId,
           t.name as templateName,
           p.status as pointStatus,
           t.price,
           p.method as paymentMethod,
           u.amount as totalPoint,
           p.bank,
           p.accountNumber,
           p.createdAt as createdDt
        )
    FROM Point p
    JOIN p.user u
    JOIN p.template t
    WHERE u.userId = :userId
      AND p.id = :pointId
""")
  PointDetailResponse findMyPointByPointId(
      @Param("userId") Long userId,
      @Param("pointId") Long pointId);
}
