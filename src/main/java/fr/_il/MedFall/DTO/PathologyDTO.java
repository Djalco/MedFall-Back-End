package fr._il.MedFall.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathologyDTO {
    private Long id;
    private String name;
    private String description;
    private Long targetId;
    private String targetName;
    private List<RuleDTO> rules; // List of associated rules
    private Integer ruleCount; // Count of associated rules
}