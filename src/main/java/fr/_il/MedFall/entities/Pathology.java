package fr._il.MedFall.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pathology")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pathology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Target target;

    @OneToMany(mappedBy = "pathology", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Rule> rules = new ArrayList<>();
}