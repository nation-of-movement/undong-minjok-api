package com.undongminjok.api.daily_workout_records.controller;

import com.undongminjok.api.daily_workout_records.dto.response.DailyRecordResponse;
import com.undongminjok.api.daily_workout_records.service.DailyRecordQueryService;
import com.undongminjok.api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class DailyRecordQueryController {

  private final DailyRecordQueryService dailyRecordQueryService;

  @GetMapping
  public DailyRecordResponse getWorkoutByDate(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return dailyRecordQueryService.getWorkoutByDate(user.getUserId(), date);
  }
}
