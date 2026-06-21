package com.breads.minds.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_programs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HealthProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "dedicated_for", length = 100)
    private String dedicatedFor;

    @Column(columnDefinition = "TEXT")
    private String objective;
}
