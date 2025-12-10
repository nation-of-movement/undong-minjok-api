package com.undongminjok.api.templates.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.security.CustomUserDetails;
import com.undongminjok.api.templates.dto.TemplatePurchaseResponseDTO;
import com.undongminjok.api.templates.service.TemplatePurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
public class TemplatePurchaseController {

  private final TemplatePurchaseService templatePurchaseService;

  @PostMapping("/{templateId}/purchase")
  public ResponseEntity<ApiResponse<TemplatePurchaseResponseDTO>> purchase(
      @PathVariable Long templateId,
      @AuthenticationPrincipal CustomUserDetails userDetails // 너 프로젝트에 맞는 클래스 이름으로 교체
  ) {

    Long userId = userDetails.getUserId(); // 또는 getId()

    TemplatePurchaseResponseDTO response =
        templatePurchaseService.purchase(templateId, userId);

    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
