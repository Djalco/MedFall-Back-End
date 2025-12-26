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

import fr._il.MedFall.DTO.RuleDTO;
import fr._il.MedFall.enums.ConditionStrategy;
import fr._il.MedFall.services.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rules")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class RuleController {

    private final RuleService ruleService;

    @GetMapping
    public ResponseEntity<List<RuleDTO>> getAllRules() {
        List<RuleDTO> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleDTO> getRuleById(@PathVariable Long id) {
        RuleDTO rule = ruleService.getRuleById(id);
        return ResponseEntity.ok(rule);
    }

    @GetMapping("/label/{label}")
    public ResponseEntity<RuleDTO> getRuleByLabel(@PathVariable String label) {
        RuleDTO rule = ruleService.getRuleByLabel(label);
        return ResponseEntity.ok(rule);
    }

    @PostMapping
    public ResponseEntity<RuleDTO> createRule(@RequestBody RuleDTO ruleDTO) {
        RuleDTO createdRule = ruleService.createRule(ruleDTO);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleDTO> updateRule(@PathVariable Long id, @RequestBody RuleDTO ruleDTO) {
        RuleDTO updatedRule = ruleService.updateRule(id, ruleDTO);
        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RuleDTO>> searchRules(@RequestParam String label) {
        List<RuleDTO> rules = ruleService.searchRules(label);
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/strategy/{strategy}")
    public ResponseEntity<List<RuleDTO>> getRulesByStrategy(@PathVariable ConditionStrategy strategy) {
        List<RuleDTO> rules = ruleService.getRulesByStrategy(strategy);
        return ResponseEntity.ok(rules);
    }

    // Endpoints pour la gestion des relations avec Pathology
    @GetMapping("/pathology/{pathologyId}")
    public ResponseEntity<List<RuleDTO>> getRulesByPathology(@PathVariable Long pathologyId) {
        List<RuleDTO> rules = ruleService.getRulesByPathology(pathologyId);
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/{ruleId}/assign-pathology/{pathologyId}")
    public ResponseEntity<RuleDTO> assignPathologyToRule(
            @PathVariable Long ruleId,
            @PathVariable Long pathologyId) {
        RuleDTO updatedRule = ruleService.assignPathologyToRule(ruleId, pathologyId);
        return ResponseEntity.ok(updatedRule);
    }

    @PostMapping("/{ruleId}/remove-pathology")
    public ResponseEntity<RuleDTO> removePathologyFromRule(@PathVariable Long ruleId) {
        RuleDTO updatedRule = ruleService.removePathologyFromRule(ruleId);
        return ResponseEntity.ok(updatedRule);
    }

    @GetMapping("/pathology/{pathologyId}/count")
    public ResponseEntity<Long> countRulesByPathology(@PathVariable Long pathologyId) {
        Long count = ruleService.countRulesByPathology(pathologyId);
        return ResponseEntity.ok(count);
    }
}
