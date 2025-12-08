package com.undongminjok.api.user.service.provider;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserProviderServiceImpl implements UserProviderService{

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
      return  userRepository.findById(userId)
          .orElse(null);
  }

  /**
   * user amount 수정
   * @param userId
   * @param updateAccount
   * @return
   */
  @Override
  public Integer modifyUserAccount(Long userId, Integer updateAccount) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    Integer orgAccount = user.getAmount();  // 기존 금액
    Integer newAccount = orgAccount + updateAccount; // 새로운 금액

    // 수정
    user.updateAccount(newAccount);
    userRepository.save(user);

    return 1;

  }


}
