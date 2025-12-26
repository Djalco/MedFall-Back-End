package fr._il.MedFall.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "molecule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Molecule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Float halfLifeHours;

    private Integer standardDurationWeeks;

    @OneToOne(mappedBy = "molecule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AttributeCondition attributeCondition;

    @ManyToMany(mappedBy = "molecules", fetch = FetchType.LAZY)
    private Set<MedicationClass> medicationClasses = new HashSet<>();

}
