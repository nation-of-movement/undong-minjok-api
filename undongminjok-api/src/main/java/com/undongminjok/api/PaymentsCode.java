package com.undongminjok.api;

import java.security.SecureRandom;

public class PaymentsCode {

  private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String createPaymentId() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      int index = RANDOM.nextInt(CHAR_POOL.length());
      sb.append(CHAR_POOL.charAt(index));
    }
    return sb.toString();
  }

}
