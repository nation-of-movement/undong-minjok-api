package com.undongminjok.api.templates.controller;

import com.undongminjok.api.templates.service.TemplateRecommendService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Template Recommend",
    description = "템플릿 추천, 추천 취소 및 추천 여부 조회 API"
)
@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateRecommendController {

  private final TemplateRecommendService recommendService;

  //  추천
  @PostMapping("/{templateId}/recommend")
  public String recommend(@PathVariable Long templateId) {
    recommendService.recommend(templateId);
    return "추천 성공";
  }

  //  추천 취소
  @DeleteMapping("/{templateId}/recommend")
  public String cancel(@PathVariable Long templateId) {
    recommendService.cancel(templateId);
    return "추천 취소";
  }

  // 추천 여부 조회
  @GetMapping("/{templateId}/recommend/check")
  public boolean check(@PathVariable Long templateId,
      @RequestParam Long userId) {
    return recommendService.isRecommended(templateId, userId);
  }

  // ⭐ 유저가 추천한 템플릿 목록 조회
  @GetMapping("/recommend/user/{userId}")
  public Object getUserRecommended(@PathVariable Long userId) {
    return recommendService.getUserRecommendedTemplates(userId);
  }
}
