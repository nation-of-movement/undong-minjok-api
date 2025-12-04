package com.undongminjok.api.user.service;

import com.undongminjok.api.auth.dto.ResetPasswordRequest;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.AuthRedisService;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.dto.UserCreateRequest;
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

    User user = User.createUser(
        request.getLoginId(),
        passwordEncoder.encode(request.getPassword()),
        request.getNickname(),
        request.getEmail()
    );

    userRepository.save(user);
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
}
