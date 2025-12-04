package com.undongminjok.api.daily_workout_records.controller;

import com.undongminjok.api.daily_workout_records.dto.command.CreateDailyRecordCommand;
import com.undongminjok.api.daily_workout_records.dto.response.InitRecordResponse;
import com.undongminjok.api.daily_workout_records.service.DailyRecordCommandService;
import com.undongminjok.api.daily_workout_records.service.DailyRecordInitCommandService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class DailyRecordCommandController {
  private final DailyRecordInitCommandService dailyRecordInitCommandService;
  private final DailyRecordCommandService dailyRecordCommandService;

  //날짜 선택 시 빈 기록 자동 생성
  @PostMapping("/{date}/init")
  @PreAuthorize("isAuthenticated()")
  public InitRecordResponse initDailyRecord(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return dailyRecordInitCommandService.initRecord(date);
  }

  //운동일지 등록
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public void creatOrUpdateRecord(@RequestBody CreateDailyRecordCommand command){
    dailyRecordCommandService.createRecord(command);
  }
}
