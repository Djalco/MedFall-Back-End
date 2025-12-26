package fr._il.MedFall.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class_count_condition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassCountCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String comparator;

    @Column(nullable = true)
    private Integer threshold;

    @Column(name = "distinct_molecules")
    private Boolean distinctMolecules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_class_id")
    private MedicationClass medicationClass;
}