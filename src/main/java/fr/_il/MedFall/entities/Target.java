package fr._il.MedFall.entities;

import fr._il.MedFall.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "target")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType type;

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pathology> pathologies = new ArrayList<>();
}