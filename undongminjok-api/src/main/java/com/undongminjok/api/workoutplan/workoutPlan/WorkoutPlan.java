package com.undongminjok.api.workoutplan.workoutPlan;

import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExercise;
import jakarta.persistence.*;
import lombok.*;

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

  @OneToOne(mappedBy = "workoutPlan", fetch = FetchType.LAZY)
  private Template template;

  @OneToMany(mappedBy = "workoutPlan",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<WorkoutPlanExercise> exercises = new ArrayList<>();

  public void setTemplate(Template template) {
    this.template = template;
  }

  public void addExercise(WorkoutPlanExercise ex) {
    exercises.add(ex);
    ex.setWorkoutPlan(this);
  }
}
