package com.undongminjok.api.workoutplan.workoutPlan;

import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutExerciseCreateDTO;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExercise;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExerciseDTO;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExerciseRepository;
import com.undongminjok.api.workoutplan.WorkoutPlanRepository;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutPlanService {

  private final WorkoutPlanRepository workoutPlanRepository;
  private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
  private final TemplateRepository templateRepository;
  private final EquipmentRepository equipmentRepository;

   // 7일치 운동계획 저장
  @Transactional
  public Long saveWeeklyPlan(WorkoutPlanCreateRequest request) {

    WorkoutPlan plan = new WorkoutPlan();

    // 템플릿 연동 (선택)
    if (request.getTemplateId() != null) {
      Template template = templateRepository.findById(request.getTemplateId())
          .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));
      plan.setTemplate(template);
    }

    // 운동들 저장
    for (WorkoutExerciseCreateDTO dto : request.getExercises()) {

      Equipment equipment = null;
      if (dto.getEquipmentId() != null) {
        equipment = equipmentRepository.findById(dto.getEquipmentId())
            .orElseThrow(() -> new IllegalArgumentException("장비 없음"));
      }

      WorkoutPlanExercise exercise = new WorkoutPlanExercise(
          null,
          dto.getDay(),
          dto.getName(),
          dto.getPart(),
          dto.getReps(),
          dto.getWeight(),
          dto.getDuration(),
          dto.getOrderIndex(),
          equipment,                       // addExercise 에서 workoutPlan 세팅
          null
      );

      plan.addExercise(exercise);
    }

    return workoutPlanRepository.save(plan).getId();
  }

   // 템플릿 ID로 7일치 운동계획 전체 조회 (day 기준으로 묶어서)
  public Map<Integer, List<WorkoutPlanExerciseDTO>> getWeeklyPlan(Long templateId) {

    List<WorkoutPlanExercise> list =
        workoutPlanExerciseRepository.findAllByTemplateId(templateId);

    return list.stream()
        .map(WorkoutPlanExerciseDTO::from)
        .collect(Collectors.groupingBy(
            WorkoutPlanExerciseDTO::getDay,
            TreeMap::new,
            Collectors.toList()
        ));
  }

   // 템플릿 ID + 특정 day(1~7) 운동계획 조회
  public List<WorkoutPlanExerciseDTO> getDayPlan(Long templateId, Integer day) {
    return workoutPlanExerciseRepository.findByTemplateIdAndDay(templateId, day)
        .stream()
        .map(WorkoutPlanExerciseDTO::from)
        .toList();
  }

   // 템플릿 → Plan 엔티티 자체가 필요할 때
  public WorkoutPlan findByTemplateId(Long templateId) {
    return workoutPlanRepository.findByTemplateId(templateId)
        .orElseThrow(() -> new IllegalArgumentException("해당 템플릿의 운동계획 없음"));
  }
}
