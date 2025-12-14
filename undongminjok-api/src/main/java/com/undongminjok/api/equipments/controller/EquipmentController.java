package com.undongminjok.api.equipments.controller;

import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.equipments.dto.response.EquipmentResponse;
import com.undongminjok.api.equipments.service.EquipmentService;
import com.undongminjok.api.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
    name = "Equipment",
    description = "운동 기구 조회 API"
)
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    //부위에 따른 운동기구 조회
    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<EquipmentResponse>> getEquipment(
            @RequestParam("part") Long partId
    ) {
        return ApiResponse.success(
                equipmentService.getEquipmentsByPart(partId)
        );
    }
  }
