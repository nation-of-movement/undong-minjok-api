package com.undongminjok.api.daily_workout_records.controller;

import com.undongminjok.api.daily_workout_records.dto.request.CreateDailyRecordRequest;
import com.undongminjok.api.daily_workout_records.dto.response.DailyRecordResponse;
import com.undongminjok.api.daily_workout_records.dto.response.InitRecordResponse;
import com.undongminjok.api.daily_workout_records.service.DailyWorkoutRecordService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class DailyWorkoutRecordController {
  private final DailyWorkoutRecordService dailyWorkoutRecordService;

  //날짜 선택 시 빈 기록 자동 생성
  @PostMapping("/{date}/init")
  @PreAuthorize("isAuthenticated()")
  public InitRecordResponse initDailyRecord(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return dailyWorkoutRecordService.initRecord(date);
  }

  //운동일지 등록
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public void creatOrUpdateRecord(@RequestBody CreateDailyRecordRequest command){
    dailyWorkoutRecordService.createRecord(command);
  }


  //운동일지 조회
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public DailyRecordResponse getWorkoutByDate(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return dailyWorkoutRecordService.getWorkoutByDate(date);
  }
}
