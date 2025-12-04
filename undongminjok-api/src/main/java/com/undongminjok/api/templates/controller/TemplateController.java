package com.undongminjok.api.templates.controller;

import com.undongminjok.api.templates.dto.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateResponseDTO;
import com.undongminjok.api.templates.dto.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.service.TemplateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
  public List<TemplateListDTO> getTemplatesByName(@RequestParam String name) {
    return templateService.findByTemplateName(name);
  }

  @GetMapping("/{id}")
  public TemplateDetailDTO getTemplateDetail(@PathVariable Long id) {
    return templateService.getTemplateDetail(id);
  }

  @PostMapping
  public TemplateDetailDTO createTemplate(@RequestBody TemplateCreateRequestDTO req) {
    return templateService.createTemplate(
        req.getPicture(),
        req.getName(),
        req.getContent(),
        req.getPrice()
    );
  }

  @PatchMapping("/{id}")
  public TemplateDetailDTO updateTemplate(
      @PathVariable Long id,
      @RequestBody TemplateUpdateRequestDTO req
  ) {
    return templateService.updateTemplate(
        id,
        req.getPicture(),
        req.getContent(),
        req.getPrice()
    );
  }

  @DeleteMapping("/{id}")
  public void deleteTemplate(@PathVariable Long id) {
    templateService.deleteTemplate(id);
  }
}
