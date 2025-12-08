package com.undongminjok.api.templates.repository;

import com.undongminjok.api.templates.domain.Template;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TemplateRepository extends JpaRepository<Template, Long> {

  // 템플릿 이름에 keyword가 포함된 모든 템플릿 검색
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

}
