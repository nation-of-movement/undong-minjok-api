package com.undongminjok.api.parts.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.parts.dto.response.PartResponse;
import com.undongminjok.api.parts.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<PartResponse>> getAllParts() {
        return ApiResponse.success(partService.getAllParts());
    }
}
