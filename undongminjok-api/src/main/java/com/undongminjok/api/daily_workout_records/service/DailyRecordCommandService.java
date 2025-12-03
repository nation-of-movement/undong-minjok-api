package com.undongminjok.api.daily_workout_records.service;

import com.undongminjok.api.daily_workout_exercises.domain.DailyWorkoutExercise;
import com.undongminjok.api.daily_workout_exercises.repository.DailyWorkoutExerciseRepository;
import com.undongminjok.api.daily_workout_records.DailyRecordErrorCode;
import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import com.undongminjok.api.daily_workout_records.dto.command.CreateDailyRecordCommand;
import com.undongminjok.api.daily_workout_records.repository.DailyWorkoutRecordRepository;
import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.security.CustomUserDetails;
import com.undongminjok.api.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyRecordCommandService {
  private final DailyWorkoutRecordRepository recordRepo;
  private final DailyWorkoutExerciseRepository exerciseRepo;
  private final EquipmentRepository equipmentRepo;
  private final UserRepository userRepo;

  public void createRecord(CreateDailyRecordCommand request) {

    //로그인한 유저 가져오기
    CustomUserDetails user =
        (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    Long userId = user.getUserId();

    //빈 리스트이면 에러 및 저장 안함
    if (request.getExercises() == null || request.getExercises().isEmpty()) {
      throw new BusinessException(DailyRecordErrorCode.EMPTY_EXERCISE_LIST);
    }

    //기존 기록 조회
    DailyWorkoutRecord record = recordRepo.findByUserUserIdAndDate(userId, request.getDate())
        .orElseGet(() -> saveNewRecord(userId, request.getDate()));

    //새로운 이미지가 있으면 업데이트
    if (request.getWorkoutImgPath() != null && !request.getWorkoutImgPath().isBlank()) {
      record.updateImg(request.getWorkoutImgPath());
    }
    //기존 운동 기록 삭제 -> 기존 + 수정내용 다시 insert 순서 변경을 반영하기 위해
    exerciseRepo.deleteByWorkoutRecord(record);

    //새 운동들 등록(orderIndex 서버에서 다시 부여)
    int index = 0;
    List<DailyWorkoutExercise> exercises = new ArrayList<>();

    for (var ex: request.getExercises()) {

      index++;

      exercises.add(
          DailyWorkoutExercise.builder()
              .name(ex.getExerciseName())
              .part(ex.getPart())
              .reps(ex.getReps())
              .weight(ex.getWeight())
              .duration(ex.getDuration())
              .equipment(getOptionalEquipment(ex.getEquipmentId()))
              .orderIndex(index)
              .workoutRecord(record)
              .build()
      );
    }

    exerciseRepo.saveAll(exercises);
  }

  private Equipment getOptionalEquipment(Long equipmentId) {
    if (equipmentId == null) {
      return null; // 장비 선택 안함 //null값 들어가도록
    }
    //잘못된 장비 id면 오류처리
    return equipmentRepo.findById(equipmentId)
        .orElseThrow(() ->
            new BusinessException(DailyRecordErrorCode.INVALID_EQUIPMENT_ID));
  }

  //해당날짜에 아무 일지 기록도 없으면 새로운 운동일지를 만듦
  private DailyWorkoutRecord saveNewRecord(Long userId, LocalDate date) {
    return recordRepo.save(
        DailyWorkoutRecord.builder()
            .date(date)
            .user(userRepo.getReferenceById(userId))
            .build()
    );
  }
}
