package com.undongminjok.api.template_storage.repository;

import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.templates.dto.TemplatePurchaseHistoryDTO;
import java.util.List;
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
}
