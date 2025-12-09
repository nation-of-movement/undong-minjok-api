package com.undongminjok.api.templates.service;

import com.undongminjok.api.point.repository.PointRepository;
import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.template_storage.repository.TemplateStorageRepository;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.dto.TemplatePurchaseHistoryDTO;
import com.undongminjok.api.templates.dto.TemplatePurchaseResponseDTO;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.repository.UserRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TemplatePurchaseService {

  private final TemplateRepository templateRepository;
  private final UserRepository userRepository;
  private final TemplateStorageRepository templateStorageRepository;

  //내 구매내역조회
  @Transactional(readOnly = true)
  public List<TemplatePurchaseHistoryDTO> getMyPurchases(Long userId) {
    return templateStorageRepository.findPurchaseHistoryByUserId(userId);
  }

  @Transactional
  public TemplatePurchaseResponseDTO purchase(Long templateId, Long userId) {

    // 1. 템플릿 / 유저 조회
    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("템플릿이 존재하지 않습니다."));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

    // 2. 이미 보관함에 있으면 예외 (ID 기반)
    if (templateStorageRepository.existsByUserUserIdAndTemplateId(userId, templateId)) {
      throw new IllegalStateException("이미 구매한 템플릿입니다.");
    }

    // 3. 포인트 비교
    long priceLong = template.getPrice();      // Template.price : Long
    int price = Math.toIntExact(priceLong);   // Integer 로 변환 (범위 벗어나면 예외)

    user.usePoint(price);                    // User.amount 차감 (포인트 부족하면 예외)

    // 4. 판매량 증가
    template.increaseSales();

    // 5. 보관함 저장
    TemplateStorage storage = TemplateStorage.builder()
        .user(user)
        .template(template)
        .build();
    templateStorageRepository.save(storage);

    // 8. 응답 DTO
    return new TemplatePurchaseResponseDTO(
        template.getId(),
        template.getName(),
        template.getPrice()
    );
  }
}
