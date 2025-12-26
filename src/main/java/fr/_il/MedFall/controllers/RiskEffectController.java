package fr._il.MedFall.controllers;

import fr._il.MedFall.DTO.RiskEffectDTO;
import fr._il.MedFall.services.RiskEffectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("risk-effects")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class RiskEffectController {

    private final RiskEffectService riskEffectService;

    // =========================================================
    // CRUD BASIQUE
    // =========================================================

    @GetMapping
    public ResponseEntity<List<RiskEffectDTO>> getAllRiskEffects() {
        List<RiskEffectDTO> dtos = riskEffectService.getAllRiskEffects();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiskEffectDTO> getRiskEffectById(@PathVariable Long id) {
        RiskEffectDTO dto = riskEffectService.getRiskEffectById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<RiskEffectDTO> createRiskEffect(@RequestBody RiskEffectDTO riskEffectDTO) {
        RiskEffectDTO createdDto = riskEffectService.createRiskEffect(riskEffectDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiskEffectDTO> updateRiskEffect(@PathVariable Long id, @RequestBody RiskEffectDTO riskEffectDTO) {
        RiskEffectDTO updatedDto = riskEffectService.updateRiskEffect(id, riskEffectDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRiskEffect(@PathVariable Long id) {
        riskEffectService.deleteRiskEffect(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // MÉTHODES LIÉES À RULE
    // =========================================================

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<RiskEffectDTO>> getRiskEffectsByRuleId(@PathVariable Long ruleId) {
        List<RiskEffectDTO> dtos = riskEffectService.getRiskEffectsByRuleId(ruleId);
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/rule/{ruleId}")
    public ResponseEntity<Void> deleteRiskEffectsByRuleId(@PathVariable Long ruleId) {
        riskEffectService.deleteRiskEffectsByRuleId(ruleId);
        return ResponseEntity.noContent().build();
    }
}
