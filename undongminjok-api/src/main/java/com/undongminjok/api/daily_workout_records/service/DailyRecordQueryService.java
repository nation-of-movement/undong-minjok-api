package com.undongminjok.api.daily_workout_records.service;

import com.undongminjok.api.daily_workout_exercises.domain.DailyWorkoutExercise;
import com.undongminjok.api.daily_workout_exercises.dto.response.ExerciseResponse;
import com.undongminjok.api.daily_workout_exercises.repository.DailyWorkoutExerciseRepository;
import com.undongminjok.api.daily_workout_records.DailyRecordErrorCode;
import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import com.undongminjok.api.daily_workout_records.dto.response.DailyRecordResponse;
import com.undongminjok.api.daily_workout_records.repository.DailyWorkoutRecordRepository;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.security.CustomUserDetails;
import com.undongminjok.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyRecordQueryService {

  private final DailyWorkoutRecordRepository recordRepo;
  private final DailyWorkoutExerciseRepository exerciseRepo;

  public DailyRecordResponse getWorkoutByDate(LocalDate date) {

    //userid 가져오기 수정
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    //기록 조회
    DailyWorkoutRecord record = recordRepo.findByUserUserIdAndDate(userId, date)
        .orElseThrow(() ->
            new BusinessException(DailyRecordErrorCode.WORKOUT_RECORD_NOT_FOUND)
        );

    //운동 리스트 조회
    List<DailyWorkoutExercise> exercises =
        exerciseRepo.findByWorkoutRecordOrderByOrderIndexAsc(record);

    //exercise목록 dto변환
    List<ExerciseResponse> exerciseResponses = exercises.stream()
        .map(ex -> ExerciseResponse.builder()
            .exerciseId(ex.getId())
            .exerciseName(ex.getName())
            .exercisePart(ex.getPart())
            .equipmentName(ex.getEquipment() != null ?
                ex.getEquipment().getName() : null)
            .reps(ex.getReps())
            .weight(ex.getWeight())
            .duration(ex.getDuration())
            .orderIndex(ex.getOrderIndex())
            .build()
        ).toList();

    return DailyRecordResponse.builder()
        .recordId(record.getId())
        .date(record.getDate())
        .workoutImg(record.getImgPath())
        .exercises(exerciseResponses)
        .build();
  }
}
