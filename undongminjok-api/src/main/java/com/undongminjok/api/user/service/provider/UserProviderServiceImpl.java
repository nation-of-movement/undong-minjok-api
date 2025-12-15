package com.undongminjok.api.user.service.provider;


import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.dto.UserProfileResponse;
import com.undongminjok.api.user.repository.UserRepository;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserProviderServiceImpl implements UserProviderService {

  private final UserRepository userRepository;

  /**
   * user가 가지고 있는 총 금액 조회
   * @param userId
   * @return
   */
  @Override
  public Integer getUserAccount(Long userId) {

    return userRepository.findById(userId)
                         .map(User::getAmount)
                         .orElse(0);
  }

  /**
   * user 정보 조회
   * @param userId
   * @return
   */
  @Override
  public User getUser(Long userId) {
    return userRepository.findById(userId)
                         .orElse(null);
  }

  @Override
  public User findByIdForUpdate(Long userId) {
    return userRepository.findByIdForUpdate(userId)
                         .orElse(null);
  }

  @Override
  public UserProfileResponse getUserProfile(Long userId) {
    User user = userRepository.findById(userId)
                              .orElse(null);
    return UserProfileResponse.builder()
                              .bio(Objects.requireNonNull(user)
                                          .getBio())
                              .nickname(user.getNickname())
                              .profileImagePath(
                                  user.getProfileImagePath())
                              .build();
  }
}
