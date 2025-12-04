package com.undongminjok.api.daily_workout_records.service;

import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import com.undongminjok.api.daily_workout_records.dto.response.InitRecordResponse;
import com.undongminjok.api.daily_workout_records.repository.DailyWorkoutRecordRepository;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.user.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyRecordInitCommandService {
  private final DailyWorkoutRecordRepository recordRepo;
  private final UserRepository userRepo;

  public InitRecordResponse initRecord(LocalDate date) {
    //로그인한 유저 가져오기
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    var existing = recordRepo.findByUserUserIdAndDate(userId, date);

    //이미 기록이 존재하는 경우
    if (existing.isPresent()) {
      DailyWorkoutRecord record = existing.get();
      return InitRecordResponse.builder()
          .recordId(record.getId())
          .date(record.getDate())
          .isNew(false)
          .build();
    }

    //기록이 존재하지 않으면 새로 생성
    DailyWorkoutRecord newRecord = recordRepo.save(
        DailyWorkoutRecord.builder()
            .date(date)
            .user(userRepo.getReferenceById(userId))
            .build()
    );

    return InitRecordResponse.builder()
        .recordId(newRecord.getId())
        .date(newRecord.getDate())
        .isNew(true)
        .build();
  }
}
