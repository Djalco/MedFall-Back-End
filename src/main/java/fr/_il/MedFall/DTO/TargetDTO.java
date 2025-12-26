package fr._il.MedFall.DTO;

import fr._il.MedFall.enums.TargetType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetDTO {
    private Long id;
    private String name;
    private TargetType type;
    private List<PathologyDTO> pathologies; // List of associated pathologies
    private Integer pathologyCount; // Count of associated pathologies
}