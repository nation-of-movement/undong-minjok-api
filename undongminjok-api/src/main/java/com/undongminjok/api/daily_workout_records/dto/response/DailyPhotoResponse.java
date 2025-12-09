package com.undongminjok.api.daily_workout_records.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyPhotoResponse {
    private int day;
    private String workoutImg;
}

