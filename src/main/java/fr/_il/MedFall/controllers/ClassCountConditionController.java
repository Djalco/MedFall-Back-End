package fr._il.MedFall.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr._il.MedFall.DTO.ClassCountConditionDTO;
import fr._il.MedFall.services.ClassCountConditionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("class-count-conditions")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class ClassCountConditionController {

    private final ClassCountConditionService classCountConditionService;

    @GetMapping
    public ResponseEntity<List<ClassCountConditionDTO>> getAllClassCountConditions() {
        return ResponseEntity.ok(classCountConditionService.getAllClassCountConditions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassCountConditionDTO> getClassCountConditionById(@PathVariable Long id) {
        return ResponseEntity.ok(classCountConditionService.getClassCountConditionById(id));
    }

    @PostMapping
    public ResponseEntity<ClassCountConditionDTO> create(@RequestBody ClassCountConditionDTO dto) {
        ClassCountConditionDTO created = classCountConditionService.createClassCountCondition(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassCountConditionDTO> updateClassCountCondition(
            @PathVariable Long id,
            @RequestBody ClassCountConditionDTO dto
    ) {
        return ResponseEntity.ok(classCountConditionService.updateClassCountCondition(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassCountCondition(@PathVariable Long id) {
        classCountConditionService.deleteClassCountCondition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<ClassCountConditionDTO>> getConditionsByRuleId(@PathVariable Long ruleId) {
        return ResponseEntity.ok(classCountConditionService.getClassCountConditionsByRuleId(ruleId));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ClassCountConditionDTO> getConditionByMedicationClassId(@PathVariable Long classId) {
        return ResponseEntity.ok(classCountConditionService.getConditionByMedicationClassId(classId));
    }

    // --- Supprimer toutes les conditions liées à une MedicationClass ---
    @DeleteMapping("/class/{classId}")
    public ResponseEntity<Void> deleteByMedicationClassId(@PathVariable Long classId) {
        classCountConditionService.deleteByMedicationClassId(classId);
        return ResponseEntity.noContent().build();
    }

    // --- Supprimer toutes les conditions liées à une Rule ---
    @DeleteMapping("/rule/{ruleId}")
    public ResponseEntity<Void> deleteByRuleId(@PathVariable Long ruleId) {
        classCountConditionService.deleteByRuleId(ruleId);
        return ResponseEntity.noContent().build();
    }
}
