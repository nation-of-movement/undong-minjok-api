package com.undongminjok.api.templates.repository;

import com.undongminjok.api.templates.domain.Template;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TemplateRepository extends JpaRepository<Template, Long> {

  // 템플릿 이름에 keyword가 포함된 모든 템플릿 검색
  List<Template> findByNameContaining(String keyword);

  // 상세조회: 템플릿 + workoutPlan + 7일 운동 전부 fetch join
  @Query("""
        select t from Template t
        join fetch t.workoutPlan wp
        left join fetch wp.exercises
        where t.id = :id
    """)
  Optional<Template> findDetailById(Long id);

  List<Template> findAllByUserUserId(Long userId);
}