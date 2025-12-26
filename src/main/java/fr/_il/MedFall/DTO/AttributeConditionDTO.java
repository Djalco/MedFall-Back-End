package fr._il.MedFall.DTO;

import fr._il.MedFall.enums.Scope;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeConditionDTO {
    private Long id;
    private Scope scope;
    private String attribute;
    private String operator;
    private String value;
    private String unit;
    private Long ruleId;
    private String ruleLabel;
    private Long moleculeId;
    private String moleculeName;
}