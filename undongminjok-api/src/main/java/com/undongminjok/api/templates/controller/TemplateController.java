package com.undongminjok.api.templates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.templates.dto.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateDetailDTO;
import com.undongminjok.api.templates.dto.TemplateListDTO;
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

  // 템플릿 목록 조회 (이름 검색)
  @GetMapping
  public ResponseEntity<ApiResponse<List<TemplateListDTO>>> getTemplatesByName(
      @RequestParam String name
  ) {
    List<TemplateListDTO> templates = templateService.findByTemplateName(name);
    return ResponseEntity.ok(ApiResponse.success(templates));
  }

  // 템플릿 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TemplateDetailDTO>> getTemplateDetail(
      @PathVariable Long id
  ) {
    TemplateDetailDTO detail = templateService.getTemplateDetail(id);
    return ResponseEntity.ok(ApiResponse.success(detail));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> createTemplate(
      @RequestPart("data") String dataJson,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
      @RequestPart(value = "detailImage", required = false) MultipartFile detailImage
  ) throws Exception {  // ← 명시적으로 Exception 던질 수 있음 (optional)

    ObjectMapper mapper = new ObjectMapper();

    //  try/catch 없이 그대로 던지기
    TemplateCreateRequestDTO req = mapper.readValue(dataJson, TemplateCreateRequestDTO.class);

    templateService.createTemplate(req, thumbnail, detailImage);

    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * 템플릿 수정 (텍스트 + 썸네일 + 상세 이미지)
   * multipart/form-data 사용
   */
  @PatchMapping(
      value = "/{id}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<ApiResponse<Void>> updateTemplate(
      @PathVariable Long id,
      @RequestPart("data") String dataJson,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
      @RequestPart(value = "detailImage", required = false) MultipartFile detailImage
  ) throws Exception {

    ObjectMapper mapper = new ObjectMapper();

    TemplateUpdateRequestDTO req =
        mapper.readValue(dataJson, TemplateUpdateRequestDTO.class);

    templateService.updateTemplate(id, req, thumbnail, detailImage);

    return ResponseEntity.ok(ApiResponse.success(null));
  }
}


