package com.SadhyaSiddhi.ems_service.models;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String key;   // e.g. "company_name"

    private String title;
    private String description;
    private String category; // e.g. "email", "ui", "attendance"

    @Column(nullable = false)
    private String dataType; // STRING, INT, LIST, MAP, DATETIME

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String data;

}
