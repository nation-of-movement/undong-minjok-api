package com.undongminjok.api.daily_workout_records.controller;

import com.undongminjok.api.daily_workout_records.dto.command.CreateDailyRecordCommand;
import com.undongminjok.api.daily_workout_records.dto.response.DailyRecordResponse;
import com.undongminjok.api.daily_workout_records.service.DailyRecordCommandService;
import com.undongminjok.api.daily_workout_records.service.DailyRecordQueryService;
import com.undongminjok.api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class DailyRecordQueryController {

  private final DailyRecordQueryService dailyRecordQueryService;
  private final DailyRecordCommandService dailyRecordCommandService;

  //운동일지 등록
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public void creatOrUpdateRecord(@RequestBody CreateDailyRecordCommand command){
    dailyRecordCommandService.createRecord(command);
  }

  //운동일지 조회
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public DailyRecordResponse getWorkoutByDate(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return dailyRecordQueryService.getWorkoutByDate(date);
  }
}
