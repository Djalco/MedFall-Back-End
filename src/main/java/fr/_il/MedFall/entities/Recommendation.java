package fr._il.MedFall.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommendation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text_string", columnDefinition = "TEXT", nullable = false)
    private String textString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;
}