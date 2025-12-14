package com.undongminjok.api.template_storage.controller;

import com.undongminjok.api.template_storage.dto.response.TemplateStorageListResponse;
import com.undongminjok.api.template_storage.service.TemplateApplyService;
import com.undongminjok.api.template_storage.service.TemplateStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Template Storage",
    description = "템플릿 보관함 및 템플릿 적용 API"
)
@RestController
@RequestMapping("/api/v1/templates/storage")
@RequiredArgsConstructor
public class TemplateStorageController {
  private final TemplateStorageService templateStorageService;
  private final TemplateApplyService templateApplyService;

  //템플릿 저장
  @PostMapping("/{templateId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> addTemplateToStorage(@PathVariable Long templateId) {
    templateStorageService.saveTemplateToStorage(templateId);
    return ResponseEntity.ok().body("템플릿 저장 완료");
  }

  //템플릿 삭제
  @DeleteMapping("/{templateId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> removeTemplateFromStorage(@PathVariable Long templateId) {
    templateStorageService.deleteTemplateFromStorage(templateId);
    return ResponseEntity.ok().body("템플릿 삭제 완료");
  }

  //템플릿 적용
  @PostMapping("/{templateId}/apply")
  public ResponseEntity<String> applyTemplate(
      @PathVariable Long templateId,
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    templateApplyService.applyTemplate(templateId, date);
    return ResponseEntity.ok("템플릿 적용 완료");
  }

  //템플릿 보관함 조회
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<TemplateStorageListResponse> getMyStoredTemplateList() {
    return templateStorageService.getMyTemplateStorageList();
  }
}
