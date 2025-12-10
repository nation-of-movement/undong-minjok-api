package com.undongminjok.api.daily_workout_exercises.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExerciseResponse {
    private Long exerciseId;
    private String equipmentName;
    private String exerciseName;
    private String exercisePart;
    private Integer duration;
    private Integer reps;
    private Integer weight;
    private Integer orderIndex;
}
