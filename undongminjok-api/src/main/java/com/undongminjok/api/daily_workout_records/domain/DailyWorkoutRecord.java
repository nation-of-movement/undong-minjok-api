package com.undongminjok.api.daily_workout_records.domain;

import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "daily_workout_records",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "workout_date"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWorkoutRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_record_id")
    private Long id;

    //운동 날짜
    @Column(name = "workout_date", nullable = false)
    private LocalDate date;

    //운동사진
    @Column(name = "img_path")
    private String imgPath;

    //회원 ID
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateImg(String imgPath) {
      this.imgPath = imgPath;
    }

}
