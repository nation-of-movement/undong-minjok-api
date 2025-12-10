package com.undongminjok.api.daily_workout_exercises.domain;

import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.templates.domain.Template;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_workout_exercises")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWorkoutExercise extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long id;

    //운동 기구
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    //운동 이름
    @Column(name = "exercise_name", nullable = false)
    private String name;

    //운동 부위
    @Column(name = "exercise_part")
    private String part;

    //운동 시간
    @Column(name = "exercise_duration")
    private Integer duration;

    //운동 횟수
    @Column(name = "exercise_reps")
    private Integer reps;

    //중량
    @Column(name = "exercise_weight")
    private Integer weight;

    //순서 정렬용 인덱스
    @Column(name = "order_index")
    private Integer orderIndex;

    //로그 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_record_id")
    private DailyWorkoutRecord workoutRecord;

    //템플릿 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private Template template;

}
