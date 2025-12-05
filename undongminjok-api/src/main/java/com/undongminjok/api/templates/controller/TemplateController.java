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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
public class TemplateController {

  private final TemplateService templateService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TemplateListDTO>>> getTemplatesByName(@RequestParam String name) {
    List<TemplateListDTO> result = templateService.findByTemplateName(name);
    return ResponseEntity.ok(ApiResponse.success("템플릿 목록 조회 성공", result));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> getTemplateDetail(@PathVariable Long id) {
    TemplateDetailDTO detail = templateService.getTemplateDetail(id);
    return ResponseEntity.ok(ApiResponse.success("템플릿 상세 조회 성공", detail));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> createTemplate(@RequestBody TemplateCreateRequestDTO req) {
    TemplateDetailDTO result = templateService.createTemplate(req);
    return ResponseEntity.ok(ApiResponse.success("템플릿 생성 성공", result));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> updateTemplate(
      @PathVariable Long id,
      @RequestBody TemplateUpdateRequestDTO req
  ) {
    TemplateDetailDTO result = templateService.updateTemplate(id, req);
    return ResponseEntity.ok(ApiResponse.success("템플릿 수정 성공", result));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
    templateService.deleteTemplate(id);
    return ResponseEntity.ok(ApiResponse.success("템플릿 삭제 성공"));
  }
}
