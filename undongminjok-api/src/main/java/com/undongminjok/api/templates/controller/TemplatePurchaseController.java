package com.undongminjok.api.templates.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.security.CustomUserDetails;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.dto.TemplatePurchaseHistoryDTO;
import com.undongminjok.api.templates.service.TemplatePurchaseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
public class TemplatePurchaseController {

  private final TemplatePurchaseService templatePurchaseService;

  /* 내 템플릿 구매 목록 */
  @GetMapping("/purchases/me")
  public ResponseEntity<ApiResponse<List<TemplatePurchaseHistoryDTO>>> getMyPurchases() {

    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    List<TemplatePurchaseHistoryDTO> list = templatePurchaseService.getMyPurchases(userId);

    return ResponseEntity.ok(ApiResponse.success(list));
  }

  /* 템플릿 구매 */
  @PostMapping("/{templateId}/purchase")
  public ResponseEntity<ApiResponse<Void>> purchase(
      @PathVariable Long templateId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUserId(); // 필요하면 로깅용으로만 사용

    // 서비스 메서드 이름/파라미터에 맞게 호출
    templatePurchaseService.purchaseTemplate(templateId);

    return ResponseEntity.ok(ApiResponse.success(null));
  }

}
