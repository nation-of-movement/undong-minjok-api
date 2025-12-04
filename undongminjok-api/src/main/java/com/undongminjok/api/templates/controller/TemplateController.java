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

  //  1. 조회 (READ)
  //  템플릿 이름으로 리스트 조회
  @GetMapping
  public List<TemplateResponseDTO> getTemplatesByName(@RequestParam String name) {
    return templateService.findByTemplateName(name);
  }

  //  2. 등록 (CREATE)
  @PostMapping
  public TemplateResponseDTO createTemplate(@RequestBody TemplateCreateRequestDTO req) {
    return templateService.createTemplate(
        req.getPicture(),
        req.getName(),
        req.getContent(),
        req.getPrice()
    );
  }

  //  3. 수정 (UPDATE)
  @PatchMapping("/{id}")
  public TemplateResponseDTO updateTemplate(
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

  //  4. 삭제 (DELETE)
  @DeleteMapping("/{id}")
  public void deleteTemplate(@PathVariable Long id) {
    templateService.deleteTemplate(id);
  }

}
