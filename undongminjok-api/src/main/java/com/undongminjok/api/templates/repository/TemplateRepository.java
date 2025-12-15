package com.undongminjok.api.templates.repository;

import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.domain.TemplateStatus;
import com.undongminjok.api.templates.dto.TemplateSalesHistoryDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TemplateRepository extends JpaRepository<Template, Long> {

  List<Template> findByNameContaining(String keyword);

  // Template + WorkoutPlan + Exercises 를 한 번에 조회 (N+1 방지)
  @Query("""
      select distinct t
      from Template t
      left join fetch t.workoutPlan wp
      left join fetch wp.exercises ex
      where t.id = :id
      """)

  Optional<Template> findDetailById(@Param("id") Long id);

  List<Template> findAllByUserUserId(Long userId);


  // 내 판매내역조회
  @Query("""
    SELECT new com.undongminjok.api.templates.dto.TemplateSalesHistoryDTO(
        t.id,
        t.name,
        t.price,
        t.salesCount,
        t.createdAt
    )
    FROM Template t
    WHERE t.user.userId = :userId
    ORDER BY t.createdAt DESC
""")
  List<TemplateSalesHistoryDTO> findSalesHistoryByUser(@Param("userId") Long userId);

  // STOPPED 제외 전체 조회
  Page<Template> findByStatusNot(TemplateStatus status, Pageable pageable);
  Page<Template> findByNameContainingAndStatusNot(String keyword, TemplateStatus status, Pageable pageable);

  // 추천 , 판매, 최신 순 정렬
  List<Template> findAllByStatusNotOrderBySalesCountDesc(TemplateStatus status);
  List<Template> findAllByStatusNotOrderByRecommendCountDesc(TemplateStatus status);
  List<Template> findAllByStatusNotOrderByCreatedAtDesc(TemplateStatus status);

}
