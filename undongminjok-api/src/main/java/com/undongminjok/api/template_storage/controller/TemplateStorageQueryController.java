package com.undongminjok.api.template_storage.controller;

import com.undongminjok.api.template_storage.dto.response.TemplateStorageListResponse;
import com.undongminjok.api.template_storage.service.TemplateStorageQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/templates/storage")
@RequiredArgsConstructor
public class TemplateStorageQueryController {
  private final TemplateStorageQueryService queryService;

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<TemplateStorageListResponse> getMyStoredTemplateList() {
    return queryService.getMyTemplateStorageList();
  }
}
