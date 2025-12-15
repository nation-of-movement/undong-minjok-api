package com.undongminjok.api.templates.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.dto.PageRequestDto;
import com.undongminjok.api.global.dto.PageResponseDto;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.TemplateSortType;
import com.undongminjok.api.templates.dto.request.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.response.TemplateDetailResponseDTO;
import com.undongminjok.api.templates.dto.response.TemplateListResponseDTO;
import com.undongminjok.api.templates.dto.request.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateSalesHistoryDTO;
import com.undongminjok.api.templates.service.TemplateService;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
    name = "Template",
    description = "운동 템플릿 조회, 생성, 수정, 삭제 및 판매 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;

    //================================== 목록 조회 ===================================
    /*
     * 템플릿 전체 목록 조회
     * */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TemplateListResponseDTO>>> getAllTemplate() {

        return ResponseEntity.ok(
                ApiResponse.success(templateService.findAllTemplates())
        );
    }

    /*
     * 템플릿 전체 목록 페이징 조회
     * */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponseDto<TemplateListResponseDTO>>> getTemplates(
            PageRequestDto pageRequestDto,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "LATEST") String sort
    ) {
        TemplateSortType sortType = TemplateSortType.from(sort);

        PageResponseDto<TemplateListResponseDTO> result =
                templateService.getTemplatePage(pageRequestDto, name, sortType);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    //================================== 상세 조회 ===================================
    /*
     * 템플릿 상세 조회
     * */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TemplateDetailResponseDTO>> getTemplateDetail(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(templateService.getTemplateDetail(id))
        );
    }

    //================================== 템플릿 만들기 ===================================
    /*
     * 템플릿 생성
     * */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> createTemplate(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "detailImage", required = false) MultipartFile detailImage) throws Exception {

        TemplateCreateRequestDTO req =
                new ObjectMapper().readValue(dataJson, TemplateCreateRequestDTO.class);

        templateService.createTemplate(req, thumbnail, detailImage);
        return ok(ApiResponse.success(null));
    }

  @PatchMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> updateTemplate(
      @PathVariable Long id,
      @RequestBody TemplateUpdateRequestDTO req
  ) {
    templateService.updateTemplate(id, req);
    return ok(ApiResponse.success(null));
  }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ok(ApiResponse.success(null));
    }

    /**
     * 내 템플릿 판매 내역
     */
    @GetMapping("/sales/me")
    public ResponseEntity<ApiResponse<List<TemplateSalesHistoryDTO>>> getMySalesHistory() {

        Long userId = SecurityUtil.getLoginUserInfo().getUserId();

        List<TemplateSalesHistoryDTO> list =
                templateService.getMySalesHistory(userId);

        return ResponseEntity.ok(ApiResponse.success(list));
    }

}
