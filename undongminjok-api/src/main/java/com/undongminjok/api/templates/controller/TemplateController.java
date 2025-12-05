package com.undongminjok.api.templates.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.templates.dto.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateDetailDTO;
import com.undongminjok.api.templates.dto.TemplateListDTO;
import com.undongminjok.api.templates.dto.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.service.TemplateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
public class TemplateController {

  private final TemplateService templateService;

  // 🔍 템플릿 목록 조회 (이름 검색)
  @GetMapping
  public ResponseEntity<ApiResponse<List<TemplateListDTO>>> getTemplatesByName(
      @RequestParam String name
  ) {
    List<TemplateListDTO> templates = templateService.findByTemplateName(name);
    return ResponseEntity.ok(ApiResponse.success(templates));
  }

  // 🔍 템플릿 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> getTemplateDetail(
      @PathVariable Long id
  ) {
    TemplateDetailDTO detail = templateService.getTemplateDetail(id);
    return ResponseEntity.ok(ApiResponse.success(detail));
  }

  // ➕ 템플릿 생성
  @PostMapping
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> createTemplate(
      @RequestBody TemplateCreateRequestDTO req
  ) {
    TemplateDetailDTO created = templateService.createTemplate(req);
    return ResponseEntity.ok(ApiResponse.success(created));
  }

  // ✏ 템플릿 수정
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> updateTemplate(
      @PathVariable Long id,
      @RequestBody TemplateUpdateRequestDTO req
  ) {
    TemplateDetailDTO updated = templateService.updateTemplate(id, req);
    return ResponseEntity.ok(ApiResponse.success(updated));
  }

  // ❌ 템플릿 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTemplate(
      @PathVariable Long id
  ) {
    templateService.deleteTemplate(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // ⭐ 템플릿 썸네일 업로드 (리스트용 이미지)
  @PostMapping("/{id}/thumbnail")
  public ResponseEntity<ApiResponse<Void>> uploadThumbnail(
      @PathVariable Long id,
      @RequestParam("file") MultipartFile file
  ) {
    templateService.updateThumbnailImage(id, file);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // ⭐ 템플릿 상세 이미지 업로드 (미리보기 이미지)
  @PostMapping("/{id}/image")
  public ResponseEntity<ApiResponse<Void>> uploadTemplateImage(
      @PathVariable Long id,
      @RequestParam("file") MultipartFile file
  ) {
    templateService.updateTemplateImage(id, file);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
