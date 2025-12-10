package com.undongminjok.api.daily_workout_records.service;

import com.undongminjok.api.daily_workout_exercises.domain.DailyWorkoutExercise;
import com.undongminjok.api.daily_workout_exercises.dto.response.ExerciseResponse;
import com.undongminjok.api.daily_workout_exercises.repository.DailyWorkoutExerciseRepository;
import com.undongminjok.api.daily_workout_records.DailyRecordErrorCode;
import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import com.undongminjok.api.daily_workout_records.dto.request.CreateDailyRecordRequest;
import com.undongminjok.api.daily_workout_records.dto.response.DailyPhotoResponse;
import com.undongminjok.api.daily_workout_records.dto.response.DailyRecordResponse;
import com.undongminjok.api.daily_workout_records.dto.response.InitRecordResponse;
import com.undongminjok.api.daily_workout_records.repository.DailyWorkoutRecordRepository;
import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class  DailyWorkoutRecordService {
  private final DailyWorkoutRecordRepository recordRepository;
  private final DailyWorkoutExerciseRepository exerciseRepository;
  private final EquipmentRepository equipmentRepository;
  private final UserRepository userRepository;
  private final FileStorage fileStorage;

  /*기록 초기 등록*/
  @Transactional
  public InitRecordResponse initRecord(LocalDate date) {
    //로그인한 유저 가져오기
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    var existing = recordRepository.findByUserUserIdAndDate(userId, date);

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
    DailyWorkoutRecord newRecord = recordRepository.save(
        DailyWorkoutRecord.builder()
            .date(date)
            .user(userRepository.getReferenceById(userId))
            .build()
    );

    return InitRecordResponse.builder()
        .recordId(newRecord.getId())
        .date(newRecord.getDate())
        .isNew(true)
        .build();
  }

  /*기록 등록*/
  @Transactional
  public void createRecord(CreateDailyRecordRequest request) {

    //로그인한 유저 가져오기
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    //빈 리스트이면 에러 및 저장 안함
    if (request.getExercises() == null || request.getExercises().isEmpty()) {
      throw new BusinessException(DailyRecordErrorCode.EMPTY_EXERCISE_LIST);
    }

    //기존 기록 조회 //없으면 error (이미 init을 거치고 오기 때문에 새로운 기록은 만들어지기 때문)
    DailyWorkoutRecord record = recordRepository.findByUserUserIdAndDate(userId, request.getDate())
        .orElseThrow(() -> new BusinessException(DailyRecordErrorCode.RECORD_NOT_FOUND));

    //새로운 이미지가 있으면 업데이트
    if (request.getWorkoutImgPath() != null && !request.getWorkoutImgPath().isBlank()) {
      record.updateImg(request.getWorkoutImgPath());
    }
    //기존 운동 기록 삭제 -> 기존 + 수정내용 다시 insert 순서 변경을 반영하기 위해
    exerciseRepository.deleteByWorkoutRecord(record);

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

    exerciseRepository.saveAll(exercises);
  }

  private Equipment getOptionalEquipment(Long equipmentId) {
    if (equipmentId == null) {
      return null; // 장비 선택 안함 //null값 들어가도록
    }
    //잘못된 장비 id면 오류처리
    return equipmentRepository.findById(equipmentId)
        .orElseThrow(() ->
            new BusinessException(DailyRecordErrorCode.INVALID_EQUIPMENT_ID));
  }

  /*기록 조회*/
  public DailyRecordResponse getWorkoutByDate(LocalDate date) {

    //userid 가져오기 수정
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    //기록 조회
    DailyWorkoutRecord record = recordRepository.findByUserUserIdAndDate(userId, date)
        .orElseThrow(() ->
            new BusinessException(DailyRecordErrorCode.WORKOUT_RECORD_NOT_FOUND)
        );

    //운동 리스트 조회
    List<DailyWorkoutExercise> exercises =
        exerciseRepository.findByWorkoutRecordOrderByOrderIndexAsc(record);

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

  @Transactional
  public void updateWorkoutImage( LocalDate date, MultipartFile file) {

    Long userId = SecurityUtil.getLoginUserInfo()
        .getUserId();

    DailyWorkoutRecord record = recordRepository.findByUserUserIdAndDate(userId, date)
        .orElseThrow(() ->
            new BusinessException(DailyRecordErrorCode.WORKOUT_RECORD_NOT_FOUND)
        );

    String oldPath = record.getImgPath();

    //새로운 파일 경로
    String newPath = fileStorage.store(file, ImageCategory.WORKOUT);

    //트랜잭션 보상 로직
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status != STATUS_COMMITTED) {
              //새파일 삭제
              fileStorage.deleteQuietly(newPath);
            } else {
              //기존 파일 삭제
              if (oldPath != null) {
                fileStorage.deleteQuietly(oldPath);
              }
            }
          }
        }
    );

    //파일 경로 저장
    record.updateImg(newPath);
  }

    public List<DailyPhotoResponse> getMonthlyPhotos(int year, int month) {

        Long userId = SecurityUtil.getLoginUserInfo().getUserId();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<DailyWorkoutRecord> records =
                recordRepository.findAllByUserUserIdAndDateBetween(userId, start, end);

        return records.stream()
                .filter(r -> r.getImgPath() != null)
                .map(r -> new DailyPhotoResponse(
                        r.getDate().getDayOfMonth(),
                        r.getImgPath()
                ))
                .toList();
    }

}
