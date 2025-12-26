package fr._il.MedFall.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr._il.MedFall.DTO.AttributeConditionDTO;
import fr._il.MedFall.enums.Scope;
import fr._il.MedFall.services.AttributeConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("attribute-conditions")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AttributeConditionController {

    private final AttributeConditionService attributeConditionService;

    @GetMapping
    public ResponseEntity<List<AttributeConditionDTO>> getAllAttributeConditions() {
        List<AttributeConditionDTO> attributeConditions = attributeConditionService.getAllAttributeConditions();
        return ResponseEntity.ok(attributeConditions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttributeConditionDTO> getAttributeConditionById(@PathVariable Long id) {
        AttributeConditionDTO attributeCondition = attributeConditionService.getAttributeConditionById(id);
        return ResponseEntity.ok(attributeCondition);
    }

    @PostMapping
    public ResponseEntity<AttributeConditionDTO> createAttributeCondition(@RequestBody AttributeConditionDTO attributeConditionDTO) {
        AttributeConditionDTO createdAttributeCondition = attributeConditionService.createAttributeCondition(attributeConditionDTO);
        return new ResponseEntity<>(createdAttributeCondition, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttributeConditionDTO> updateAttributeCondition(@PathVariable Long id, @RequestBody AttributeConditionDTO attributeConditionDTO) {
        AttributeConditionDTO updatedAttributeCondition = attributeConditionService.updateAttributeCondition(id, attributeConditionDTO);
        return ResponseEntity.ok(updatedAttributeCondition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributeCondition(@PathVariable Long id) {
        attributeConditionService.deleteAttributeCondition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AttributeConditionDTO>> searchAttributeConditions(@RequestParam String attribute) {
        List<AttributeConditionDTO> attributeConditions = attributeConditionService.searchAttributeConditions(attribute);
        return ResponseEntity.ok(attributeConditions);
    }

    @GetMapping("/scope/{scope}")
    public ResponseEntity<List<AttributeConditionDTO>> getAttributeConditionsByScope(@PathVariable Scope scope) {
        List<AttributeConditionDTO> attributeConditions = attributeConditionService.getAttributeConditionsByScope(scope);
        return ResponseEntity.ok(attributeConditions);
    }

    // Endpoints pour la gestion des relations avec Rule
    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<AttributeConditionDTO>> getAttributeConditionsByRule(@PathVariable Long ruleId) {
        List<AttributeConditionDTO> attributeConditions = attributeConditionService.getAttributeConditionsByRule(ruleId);
        return ResponseEntity.ok(attributeConditions);
    }

    @PostMapping("/{attributeConditionId}/assign-rule/{ruleId}")
    public ResponseEntity<AttributeConditionDTO> assignRuleToAttributeCondition(
            @PathVariable Long attributeConditionId,
            @PathVariable Long ruleId) {
        AttributeConditionDTO updatedAttributeCondition = attributeConditionService.assignRuleToAttributeCondition(attributeConditionId, ruleId);
        return ResponseEntity.ok(updatedAttributeCondition);
    }

    @PostMapping("/{attributeConditionId}/remove-rule")
    public ResponseEntity<AttributeConditionDTO> removeRuleFromAttributeCondition(@PathVariable Long attributeConditionId) {
        AttributeConditionDTO updatedAttributeCondition = attributeConditionService.removeRuleFromAttributeCondition(attributeConditionId);
        return ResponseEntity.ok(updatedAttributeCondition);
    }

    // Endpoints pour la gestion des relations avec Molecule
    @GetMapping("/molecule/{moleculeId}")
    public ResponseEntity<List<AttributeConditionDTO>> getAttributeConditionsByMolecule(@PathVariable Long moleculeId) {
        List<AttributeConditionDTO> attributeConditions = attributeConditionService.getAttributeConditionsByMolecule(moleculeId);
        return ResponseEntity.ok(attributeConditions);
    }

    @PostMapping("/{attributeConditionId}/assign-molecule/{moleculeId}")
    public ResponseEntity<AttributeConditionDTO> assignMoleculeToAttributeCondition(
            @PathVariable Long attributeConditionId,
            @PathVariable Long moleculeId) {
        AttributeConditionDTO updatedAttributeCondition = attributeConditionService.assignMoleculeToAttributeCondition(attributeConditionId, moleculeId);
        return ResponseEntity.ok(updatedAttributeCondition);
    }

    @PostMapping("/{attributeConditionId}/remove-molecule")
    public ResponseEntity<AttributeConditionDTO> removeMoleculeFromAttributeCondition(@PathVariable Long attributeConditionId) {
        AttributeConditionDTO updatedAttributeCondition = attributeConditionService.removeMoleculeFromAttributeCondition(attributeConditionId);
        return ResponseEntity.ok(updatedAttributeCondition);
    }

    @GetMapping("/rule/{ruleId}/count")
    public ResponseEntity<Long> countAttributeConditionsByRule(@PathVariable Long ruleId) {
        Long count = attributeConditionService.countAttributeConditionsByRule(ruleId);
        return ResponseEntity.ok(count);
    }
}
