package com.undongminjok.api.user.service;

import com.undongminjok.api.auth.AuthErrorCode;
import com.undongminjok.api.auth.dto.ResetPasswordRequest;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.AuthRedisService;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.dto.UpdateBioRequest;
import com.undongminjok.api.user.dto.UpdateNicknameRequest;
import com.undongminjok.api.user.dto.UserCreateRequest;
import com.undongminjok.api.user.dto.UserInfoResponse;
import com.undongminjok.api.user.dto.UserProfileResponse;
import com.undongminjok.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final FileStorage fileStorage;
  private final AuthRedisService authRedisService;

  @Transactional
  public void registerUser(UserCreateRequest request) {

    Boolean exist = userRepository.existsByLoginId(request.getLoginId());

    if (exist) {
      throw new BusinessException(UserErrorCode.USER_DUPLICATED);
    }

    if (!authRedisService.isSignupVerified(request.getEmail())) {
      throw new BusinessException(AuthErrorCode.EMAIL_NOT_VERIFIED);
    }

    User user = User.createUser(
        request.getLoginId(),
        passwordEncoder.encode(request.getPassword()),
        request.getNickname(),
        request.getEmail()
    );

    userRepository.save(user);

    authRedisService.consumeSignupVerification(request.getEmail());
  }

  @Transactional
  public void updateProfileImage(MultipartFile file) {

    Long userId = SecurityUtil.getLoginUserInfo()
                              .getUserId();

    User user = userRepository.findById(userId)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    // 기존 이미지 삭제
    if (user.getProfileImagePath() != null) {
      fileStorage.deleteQuietly(user.getProfileImagePath());
    }

    // 새 이미지 저장
    String path = fileStorage.store(file, ImageCategory.PROFILE);

    // User 엔티티 업데이트
    user.updateProfileImage(path);
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    // resetToken 으로 이메일 조회 + 유효성 검증
    String email = authRedisService.getEmailByResetToken(request.getResetToken());

    // 이메일로 유저 조회
    User user = userRepository.findByEmail(email)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    // 비밀번호 변경
    user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

    // 토큰 1회용 처리 (삭제)
    authRedisService.deleteResetToken(request.getResetToken());
  }

  public UserInfoResponse getMyInfo() {
    Long userId = SecurityUtil.getLoginUserInfo()
                              .getUserId();

    User user = userRepository.findById(userId)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    return UserInfoResponse.from(user);
  }

  public UserProfileResponse getUserProfile(String loginId) {

    User user = userRepository.findByLoginId(loginId)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    return UserProfileResponse.from(user);
  }

  @Transactional
  public void deleteUser() {
    Long userId = SecurityUtil.getLoginUserInfo()
                              .getUserId();

    userRepository.deleteById(userId);
  }

  @Transactional
  public void updateBio(UpdateBioRequest request) {
    Long userId = SecurityUtil.getLoginUserInfo()
                              .getUserId();

    User user = userRepository.findById(userId)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    user.updateBio(request.getBio());
  }

  @Transactional
  public void updateNickname(UpdateNicknameRequest request) {

    Long userId = SecurityUtil.getLoginUserInfo()
                              .getUserId();

    User user = userRepository.findById(userId)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    user.updateNickname(request.getNickname());
  }

  public boolean isLoginIdExists(String loginId) {
    return userRepository.existsByLoginId(loginId);
  }

  public boolean isNicknameExists(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  public String findLoginId(String token) {

    String email = authRedisService.getEmailByResetToken(token);

    User user = userRepository.findByEmail(email)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    return user.getLoginId();
  }
}
