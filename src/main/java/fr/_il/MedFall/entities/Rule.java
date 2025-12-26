package fr._il.MedFall.entities;

import fr._il.MedFall.enums.ConditionStrategy;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionStrategy strategy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pathology_id")
    private Pathology pathology;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RiskEffect> riskEffects = new ArrayList<>();

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AttributeCondition> attributeConditions = new ArrayList<>();

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ClassCountCondition> classCountConditions = new ArrayList<>();
}