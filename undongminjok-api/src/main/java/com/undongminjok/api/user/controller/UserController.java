package com.undongminjok.api.user.controller;

import com.undongminjok.api.auth.dto.ResetPasswordRequest;
import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.user.dto.UpdateBioRequest;
import com.undongminjok.api.user.dto.UpdateNicknameRequest;
import com.undongminjok.api.user.dto.UserCreateRequest;
import com.undongminjok.api.user.dto.UserInfoResponse;
import com.undongminjok.api.user.dto.UserProfileResponse;
import com.undongminjok.api.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
    name = "User",
    description = "회원 가입, 프로필 조회 및 회원 정보 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/id")
  public ResponseEntity<ApiResponse<String>> findId(@RequestParam String token) {
    String id = userService.findLoginId(token);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

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

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<?>> resetPassword(
      @RequestBody ResetPasswordRequest request
  ) {
    userService.resetPassword(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @GetMapping("/me")
  public ApiResponse<UserInfoResponse> getMyInfo() {
    UserInfoResponse response = userService.getMyInfo();
    return ApiResponse.success(response);
  }

  @GetMapping("/{loginId}")
  public ApiResponse<UserProfileResponse> getUserProfile(@PathVariable String loginId) {
    UserProfileResponse response = userService.getUserProfile(loginId);
    return ApiResponse.success(response);
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteUser() {
    userService.deleteUser();
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PatchMapping("/nickname")
  public ApiResponse<Void> updateNickname(@RequestBody UpdateNicknameRequest request) {
    userService.updateNickname(request);
    return ApiResponse.success(null);
  }

  @PatchMapping("/bio")
  public ApiResponse<Void> updateBio(@RequestBody UpdateBioRequest request) {
    userService.updateBio(request);
    return ApiResponse.success(null);
  }

  @GetMapping("/exists/login-id")
  public ApiResponse<?> checkLoginIdDuplicate(@RequestParam String loginId) {
    boolean exists = userService.isLoginIdExists(loginId);
    return ApiResponse.success(Map.of("exists", exists));
  }

  @GetMapping("/exists/nickname")
  public ApiResponse<?> checkNicknameDuplicate(@RequestParam String nickname) {
    boolean exists = userService.isNicknameExists(nickname);
    return ApiResponse.success(Map.of("exists", exists));
  }

}
