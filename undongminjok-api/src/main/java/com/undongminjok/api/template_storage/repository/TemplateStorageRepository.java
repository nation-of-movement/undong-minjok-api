package com.undongminjok.api.template_storage.repository;

import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.dto.TemplatePurchaseHistoryDTO;
import com.undongminjok.api.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TemplateStorageRepository extends JpaRepository<TemplateStorage, Long> {

  boolean existsByUserUserIdAndTemplateId(Long userId, Long templateId);

  void deleteByUserUserIdAndTemplateId(Long userId, Long templateId);

  List<TemplateStorage> findAllByUserUserId(Long userId);


  // 내 구매내역 조회
  @Query("""
        select new com.undongminjok.api.templates.dto.TemplatePurchaseHistoryDTO(
            t.id,
            t.name,
            t.price,
            ts.createdAt
        )
        from TemplateStorage ts
        join ts.template t
        join ts.user u
        where u.userId = :userId
        order by ts.createdAt desc
        """)

  List<TemplatePurchaseHistoryDTO> findPurchaseHistoryByUserId(@Param("userId") Long userId);

  // 템플릿 구매시 이미 보관함에 존재하는지 확인
  boolean existsByUserAndTemplate(User userId, Template templateId);

  //숨기지 않은 템플릿 조회
  List<TemplateStorage> findAllByUserUserIdAndDeletedFalse(Long userId);

  List<TemplateStorage> findAllByTemplateId(Long templateId);
  Optional<TemplateStorage> findByUserUserIdAndTemplateId(Long userId, Long templateId);
}
