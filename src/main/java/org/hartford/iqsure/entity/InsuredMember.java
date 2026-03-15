package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "insured_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuredMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_policy_id", nullable = false)
    private UserPolicy userPolicy;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String relationship;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String preExistingConditions;
}
