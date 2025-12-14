package com.undongminjok.api.daily_workout_records.controller;

import com.undongminjok.api.daily_workout_records.dto.request.CreateDailyRecordRequest;
import com.undongminjok.api.daily_workout_records.dto.response.DailyPhotoResponse;
import com.undongminjok.api.daily_workout_records.dto.response.DailyRecordResponse;
import com.undongminjok.api.daily_workout_records.dto.response.InitRecordResponse;
import com.undongminjok.api.daily_workout_records.service.DailyWorkoutRecordService;
import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Tag(
    name = "Daily Workout Record",
    description = "일별 운동 기록 관리 API"
)
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class DailyWorkoutRecordController {
  private final DailyWorkoutRecordService dailyWorkoutRecordService;
  private final FileStorage fileStorage;

  //날짜 선택 시 빈 기록 자동 생성
  @PostMapping("/{date}/init")
  @PreAuthorize("isAuthenticated()")
  public InitRecordResponse initDailyRecord(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return dailyWorkoutRecordService.initRecord(date);
  }

  //사진 등록
  @PostMapping(value = "/{date}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ApiResponse<?> uploadWorkoutImage(
      @RequestParam("file") MultipartFile file,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    dailyWorkoutRecordService.updateWorkoutImage(date, file);
    return ApiResponse.success(null);
  }

  //운동일지 등록
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public void creatOrUpdateRecord(@RequestBody CreateDailyRecordRequest request){
    dailyWorkoutRecordService.createRecord(request);
  }


  //운동일지 조회
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public DailyRecordResponse getWorkoutByDate(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return dailyWorkoutRecordService.getWorkoutByDate(date);
  }

  //월별 사진 가져오기
  @GetMapping("/photos")
  @PreAuthorize("isAuthenticated()")
  public List<DailyPhotoResponse> getMonthlyPhotos(
          @RequestParam int year,
          @RequestParam int month
  ) {
      return dailyWorkoutRecordService.getMonthlyPhotos(year, month);
  }

}
