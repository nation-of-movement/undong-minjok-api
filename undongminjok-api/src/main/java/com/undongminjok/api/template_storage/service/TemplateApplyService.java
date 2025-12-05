package com.undongminjok.api.template_storage.service;

import com.undongminjok.api.daily_workout_exercises.domain.DailyWorkoutExercise;
import com.undongminjok.api.daily_workout_exercises.repository.DailyWorkoutExerciseRepository;
import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import com.undongminjok.api.daily_workout_records.repository.DailyWorkoutRecordRepository;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.TemplateErrorCode;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateApplyService {
  private final DailyWorkoutRecordRepository recordRepository;
  private final DailyWorkoutExerciseRepository exerciseRepository;
  private final TemplateRepository templateRepository;
  private final UserRepository userRepository;

  //템플릿 7일 적용
  public void applyTemplate(Long templateId, LocalDate startDate) {
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    //템플릿 검증
    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND));

    //템플릿 운동 조회
    List<DailyWorkoutExercise> templateExercises =
        exerciseRepository.findByTemplateIdOrderByOrderIndexAsc(templateId);

    if (templateExercises.isEmpty()) {
      throw new BusinessException(TemplateErrorCode.TEMPLATE_HAS_NO_EXERCISES);
    }

    //7일 반복
    for (int i = 0; i < 7; i++) {
      LocalDate targetDate = startDate.plusDays(i);

      //기록 찾기 또는 생성
      DailyWorkoutRecord record = recordRepository
          .findByUserUserIdAndDate(userId, targetDate)
          .orElseGet(() -> recordRepository.save(
              DailyWorkoutRecord.builder().date(targetDate).user(userRepository.getReferenceById(userId)).build()
          ));

      //이미 해당 날짜에 들어있는 exercise 개수 확인 -> orderIndex 이어붙이기 위함
      int baseIndex = exerciseRepository.countByWorkoutRecord(record);

      List<DailyWorkoutExercise> newExercises = new ArrayList<>();;

      for (int idx = 0; idx < templateExercises.size(); idx++){
        DailyWorkoutExercise t = templateExercises.get(idx);

        newExercises.add(
            DailyWorkoutExercise.builder()
                .name(t.getName())
                .part(t.getPart())
                .reps(t.getReps())
                .weight(t.getWeight())
                .duration(t.getDuration())
                .equipment(t.getEquipment())
                .orderIndex(baseIndex + idx + 1)
                .workoutRecord(record)
                .template(template)
                .build()
        );
      }
      exerciseRepository.saveAll(newExercises);
    }
  }
}
