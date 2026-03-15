package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "education_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

}
