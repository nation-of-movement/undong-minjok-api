package com.undongminjok.api.workoutplan.workoutPlan;

import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExercise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plans")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //  이 템플릿에 대한 7일치 계획
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id", unique = true)
  private Template template;

  // 7일 운동 리스트
  @OneToMany(mappedBy = "workoutPlan",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<WorkoutPlanExercise> exercises = new ArrayList<>();

  public void addExercise(WorkoutPlanExercise ex) {
    exercises.add(ex);
    ex.setWorkoutPlan(this);
  }

  public void setTemplate(Template template) {
    this.template = template;
  }
}