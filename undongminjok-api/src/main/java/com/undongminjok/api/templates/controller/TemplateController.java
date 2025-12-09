package com.undongminjok.api.templates.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.dto.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateDetailDTO;
import com.undongminjok.api.templates.dto.TemplateListDTO;
import com.undongminjok.api.templates.dto.TemplateSalesHistoryDTO;
import com.undongminjok.api.templates.dto.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.service.TemplateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
public class TemplateController {

  private final TemplateService templateService;

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<List<TemplateListDTO>>> getAllTemplate() {

      return ResponseEntity.ok(
          ApiResponse.success(templateService.findAllTemplates())
    );
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TemplateListDTO>>> getTemplatesByName(
      @RequestParam String name) {

    return ok(
        ApiResponse.success(templateService.findByTemplateName(name))
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> getTemplateDetail(
      @PathVariable Long id) {

    return ok(
        ApiResponse.success(templateService.getTemplateDetail(id))
    );
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> createTemplate(
      @RequestPart("data") String dataJson,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
      @RequestPart(value = "detailImage", required = false) MultipartFile detailImage) throws Exception {

    TemplateCreateRequestDTO req =
        new ObjectMapper().readValue(dataJson, TemplateCreateRequestDTO.class);

    templateService.createTemplate(req, thumbnail, detailImage);
    return ok(ApiResponse.success(null));
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> updateTemplate(
      @PathVariable Long id,
      @RequestPart("data") String dataJson,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
      @RequestPart(value = "detailImage", required = false) MultipartFile detailImage) throws Exception {

    TemplateUpdateRequestDTO req =
        new ObjectMapper().readValue(dataJson, TemplateUpdateRequestDTO.class);

    templateService.updateTemplate(id, req, thumbnail, detailImage);
    return ok(ApiResponse.success(null));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
    templateService.deleteTemplate(id);
    return ok(ApiResponse.success(null));
  }

  /** 내 템플릿 판매 내역 */
  @GetMapping("/sales/me")
  public ResponseEntity<ApiResponse<List<TemplateSalesHistoryDTO>>> getMySalesHistory() {

    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    List<TemplateSalesHistoryDTO> list =
        templateService.getMySalesHistory(userId);

    return ResponseEntity.ok(ApiResponse.success(list));
  }
}
