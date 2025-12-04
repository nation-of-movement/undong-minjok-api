package com.undongminjok.api.user.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.user.dto.UserCreateRequest;
import com.undongminjok.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;
  private final FileStorage fileStorage;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerUser(
      @RequestBody UserCreateRequest request
  ) {
    userService.registerUser(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(null));
  }

  @PostMapping("/profile-image")
  public ApiResponse<?> uploadProfile(
      @RequestParam("file") MultipartFile file
  ) {
    userService.updateProfileImage(file);
    return ApiResponse.success(null);
  }
}
