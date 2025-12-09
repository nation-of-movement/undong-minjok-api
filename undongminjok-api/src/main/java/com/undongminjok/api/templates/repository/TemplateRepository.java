package com.undongminjok.api.templates.repository;

import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.domain.TemplateStatus;
import java.util.List;
import java.util.Optional;
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

  // STOPPED 제외 전체 조회
  List<Template> findByStatusNot(TemplateStatus status);

  // 추천 , 판매, 최신 순 정렬
  List<Template> findAllByStatusNotOrderBySalesCountDesc(TemplateStatus status);
  List<Template> findAllByStatusNotOrderByRecommendCountDesc(TemplateStatus status);
  List<Template> findAllByStatusNotOrderByCreatedAtDesc(TemplateStatus status);

}
