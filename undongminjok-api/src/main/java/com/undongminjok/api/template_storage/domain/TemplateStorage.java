package com.undongminjok.api.template_storage.domain;

import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_storage")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateStorage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id")
    private Template template;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    public void markAsDeleted() {
        this.deleted = true;
    }
}
