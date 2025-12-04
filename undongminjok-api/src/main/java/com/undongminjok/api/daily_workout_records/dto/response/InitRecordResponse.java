package com.undongminjok.api.daily_workout_records.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InitRecordResponse {
   private Long recordId;
   private LocalDate date;
  /* 3가지 경우를 처리하기 위한 변수
  * 케이스1: 사용자가 기록 만들었는데 아직 운동 추가x -> false
  * 케이스2: 사용자가 운동을 다 삭제해서 0개가 됨 -> false
  * 케이스3: 사용자가 해당 날짜에 처음 진입 -> true
  * false일 경우 db 조회, true일 경우 기록 새로 생성*/
   private boolean isNew;
}
