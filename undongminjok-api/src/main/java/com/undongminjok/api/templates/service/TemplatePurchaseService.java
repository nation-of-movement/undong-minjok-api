package com.undongminjok.api.templates.service;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.point.PointErrorCode;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.point.service.provider.PointProviderService;
import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.template_storage.repository.TemplateStorageRepository;
import com.undongminjok.api.templates.TemplateErrorCode;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.dto.TemplatePurchaseHistoryDTO;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.repository.UserRepository;

import com.undongminjok.api.user.service.provider.UserProviderService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplatePurchaseService {

  private final TemplateRepository templateRepository;
  private final UserRepository userRepository;
  private final TemplateStorageRepository templateStorageRepository;
  private final UserProviderService userProviderService;
  private final PointProviderService pointProviderService;

  // 내 구매내역 조회
  @Transactional(readOnly = true)
  public List<TemplatePurchaseHistoryDTO> getMyPurchases(Long userId) {
    return templateStorageRepository.findPurchaseHistoryByUserId(userId);
  }

  @Transactional
  public void purchaseTemplate(Long templateId) {

    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND));

    Long sellerId = template.getUser().getUserId();

    // 본인이 만든 템플릿이면 구매 없이 자동 저장 후 종료
    if (sellerId.equals(userId)) {
      User user = userProviderService.getUser(userId);
      saveToStorage(user, template);
      log.info("본인 템플릿이므로 구매 없이 자동 저장 완료. userId={}, templateId={}", userId, templateId);
      return;
    }

    // 이미 구매한 템플릿인지 체크
    boolean alreadyPurchased =
        templateStorageRepository.existsByUserUserIdAndTemplateId(userId, templateId);

    if (alreadyPurchased) {
      throw new BusinessException(TemplateErrorCode.TEMPLATE_ALREADY_PURCHASED);
    }

    User user = userProviderService.getUser(userId);
    long price = template.getPrice();

    // 포인트 부족
    if (user.getAmount() < price) {
      throw new BusinessException(PointErrorCode.POINT_NOT_ENOUGH);
    }

    // 구매자 PURCHASE 이력 (차감)
    PointHistoryDTO historyDTO = PointHistoryDTO.builder()
        .userId(userId)
        .templateId(templateId)
        .status(PointStatus.PURCHASE)
        .amount((int) -price)
        .build();

    pointProviderService.createPointHistory(historyDTO);

    // 판매자 EARN 이력 (적립)
    PointHistoryDTO sellerHistory = PointHistoryDTO.builder()
        .userId(sellerId)
        .templateId(templateId)
        .status(PointStatus.EARN)
        .amount((int) price)
        .build();

    pointProviderService.createPointHistory(sellerHistory);

    // 구매한 템플릿 보관함 저장
    saveToStorage(user, template);

    // 템플릿 구매시 판매량 증가
    template.increaseSalesCount();

    log.info("템플릿 구매 완료. buyerId={}, sellerId={}, templateId={}, price={}",
        userId, sellerId, templateId, price);
  }

  // 저장 기능을 공통 처리하는 private 메서드
  private void saveToStorage(User user, Template template) {

    boolean exists = templateStorageRepository.existsByUserUserIdAndTemplateId(
        user.getUserId(),
        template.getId()
    );

    if (!exists) {
      TemplateStorage storage = TemplateStorage.builder()
          .user(user)
          .template(template)
          .deleted(false)
          .build();

      templateStorageRepository.save(storage);
    }
  }
}
