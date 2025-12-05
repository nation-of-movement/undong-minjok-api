package com.undongminjok.api.user.service.provider;

public interface UserProviderService {

  // user가 가지고 있는 총 금액 조회
  Integer getUserAccount(Long userId);

}
