package com.undongminjok.api.template_storage.controller;

import com.undongminjok.api.template_storage.dto.response.TemplateStorageListResponse;
import com.undongminjok.api.template_storage.service.TemplateStorageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/templates/storage")
@RequiredArgsConstructor
public class TemplateStorageController {
  private final TemplateStorageService templateStorageService;
  //템플릿 저장
  @PostMapping("/{templateId}")
  @PreAuthorize("isAuthenticated()")
  public void addTemplateToStorage(@PathVariable Long templateId) {
    templateStorageService.saveTemplateToStorage(templateId);
  }

  //템플릿 삭제
  @DeleteMapping("/{templateId}")
  @PreAuthorize("isAuthenticated()")
  public void removeTemplateFromStorage(@PathVariable Long templateId) {
    templateStorageService.deleteTemplateFromStorage(templateId);
  }

  //템플릿 보관함 조회
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<TemplateStorageListResponse> getMyStoredTemplateList() {
    return templateStorageService.getMyTemplateStorageList();
  }
}
