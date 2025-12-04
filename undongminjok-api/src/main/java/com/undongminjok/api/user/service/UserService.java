package com.undongminjok.api.user.service;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
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
}
