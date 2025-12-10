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
import java.util.Map;
import java.util.stream.Collectors;

import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExercise;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExerciseRepository;
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
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;

    // 템플릿 7일 적용 (기존 기록 삭제 X → append)
    public void applyTemplate(Long templateId, LocalDate startDate) {

        Long userId = SecurityUtil.getLoginUserInfo().getUserId();

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND));

        if (template.getWorkoutPlan() == null)
            throw new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND);

        //WorkoutPlanExercise를 day + orderIndex 기준으로 정렬하여 가져온다
        List<WorkoutPlanExercise> templateExercises =
                workoutPlanExerciseRepository
                        .findByWorkoutPlanTemplateIdOrderByDayAscOrderIndexAsc(templateId);

        if (templateExercises.isEmpty()) {
            throw new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND);
        }

        //day 기준으로 그룹핑
        Map<Integer, List<WorkoutPlanExercise>> grouped =
                templateExercises.stream()
                        .collect(Collectors.groupingBy(WorkoutPlanExercise::getDay));

        for (int day = 1; day <= 7; day++) {

            LocalDate targetDate = startDate.plusDays(day - 1);

            //해당 날짜에 DailyWorkoutRecord가 있는지 확인, 없으면 생성
            DailyWorkoutRecord record = recordRepository
                    .findByUserUserIdAndDate(userId, targetDate)
                    .orElseGet(() ->
                            recordRepository.save(
                                    DailyWorkoutRecord.builder()
                                            .date(targetDate)
                                            .user(userRepository.getReferenceById(userId))
                                            .build()
                            )
                    );

            //기존 운동 기록 개수 파악, 그 뒤에 이어붙이기 위해서
            int startIndex = exerciseRepository
                    .findByWorkoutRecordOrderByOrderIndexAsc(record)
                    .stream()
                    .mapToInt(DailyWorkoutExercise::getOrderIndex)
                    .max()
                    .orElse(0);

            List<WorkoutPlanExercise> dailyTemplateEx = grouped.get(day);
            if (dailyTemplateEx == null) continue;

            List<DailyWorkoutExercise> newExercises = new ArrayList<>();

            for (WorkoutPlanExercise ex : dailyTemplateEx) {

                startIndex++;

                newExercises.add(
                        DailyWorkoutExercise.builder()
                                .name(ex.getName())
                                .part(ex.getPart())
                                .reps(ex.getReps())
                                .weight(ex.getWeight())
                                .duration(ex.getDuration())
                                .equipment(ex.getEquipment())
                                .orderIndex(startIndex)  //기존 기록 뒤에 이어붙임
                                .workoutRecord(record)
                                .build()
                );
            }

            exerciseRepository.saveAll(newExercises);
        }
    }
}
