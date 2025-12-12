package com.undongminjok.api.payments.service;


import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.payments.PaymentsErrorCode;
import com.undongminjok.api.payments.dto.PaymentsRedisDTO;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentsRedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * 결제 승인시 order 정보 임시 저장
   * @param param
   */
  public void savePayments(PaymentsRedisDTO param) {
    String key = "payments:" + param.getOrderId();

    redisTemplate.opsForValue()
        .set(key, param, 30, TimeUnit.MINUTES);
  }

  /**
   * 결제 승인시 order정보 조회
   * @param param
   */
  public Boolean findPayments(PaymentsRedisDTO param) {
    String key = "payments:" + param.getOrderId();

    Boolean exists = redisTemplate.hasKey(key);
    if(Boolean.FALSE.equals(exists)) {
      throw new BusinessException(PaymentsErrorCode.PAYMENTS_ERROR_CODE);
    }

    PaymentsRedisDTO value = (PaymentsRedisDTO) redisTemplate.opsForValue().get(key);

    if (value == null) {
      throw new BusinessException(PaymentsErrorCode.PAYMENTS_ERROR_CODE);
    }

    if (! Objects.equals(value.getAmount(), param.getAmount())) {
      throw new BusinessException(PaymentsErrorCode.PAYMENTS_ERROR_CODE);
    }

    return Boolean.TRUE;

  }

  /**
   * 결제 정보 삭제
   * @param param
   */
  public void deletePayments(PaymentsRedisDTO param) {
    String key = "payments:" + param.getOrderId();
    if (redisTemplate.hasKey(key)) {
      redisTemplate.delete(key);
    }
  }
}
