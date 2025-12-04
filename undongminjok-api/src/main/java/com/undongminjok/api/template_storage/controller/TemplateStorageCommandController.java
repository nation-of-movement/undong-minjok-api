package com.undongminjok.api.template_storage.controller;

import com.undongminjok.api.template_storage.service.TemplateStorageCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/templates/storage")
@RequiredArgsConstructor
public class TemplateStorageCommandController {
  private final TemplateStorageCommandService templateStorageCommandService;

  @PostMapping("/{templateId}")
  @PreAuthorize("isAuthenticated()")
  public void addTemplateToStorage(@PathVariable Long templateId) {
    templateStorageCommandService.saveTemplateToStorage(templateId);
  }

  @DeleteMapping("/{templateId}")
  @PreAuthorize("isAuthenticated()")
  public void removeTemplateFromStorage(@PathVariable Long templateId) {
    templateStorageCommandService.deleteTemplateFromStorage(templateId);
  }
}
