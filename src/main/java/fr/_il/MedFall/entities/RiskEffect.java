package fr._il.MedFall.entities;

import fr._il.MedFall.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "risk_effect")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskEffect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;
}