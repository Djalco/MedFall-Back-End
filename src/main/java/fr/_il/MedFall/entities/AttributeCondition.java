package fr._il.MedFall.entities;

import fr._il.MedFall.enums.Scope;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attribute_condition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Scope scope;

    @Column(nullable = true)
    private String attribute;

    @Column(nullable = true)
    private String operator;

    @Column(nullable = true)
    private String value;

    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "molecule_id")
    private Molecule molecule;
}