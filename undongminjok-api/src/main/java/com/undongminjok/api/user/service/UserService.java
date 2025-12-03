package com.undongminjok.api.user.service;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.dto.UserCreateRequest;
import com.undongminjok.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

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
}
