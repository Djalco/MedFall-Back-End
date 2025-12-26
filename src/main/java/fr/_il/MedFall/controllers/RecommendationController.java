package fr._il.MedFall.controllers;

import fr._il.MedFall.DTO.RecommendationDTO;
import fr._il.MedFall.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("recommendations")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // =========================================================
    // CRUD BASIQUE
    // =========================================================

    @GetMapping
    public ResponseEntity<List<RecommendationDTO>> getAllRecommendations() {
        List<RecommendationDTO> dtos = recommendationService.getAllRecommendations();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationDTO> getRecommendationById(@PathVariable Long id) {
        RecommendationDTO dto = recommendationService.getRecommendationById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<RecommendationDTO> createRecommendation(@RequestBody RecommendationDTO dto) {
        RecommendationDTO created = recommendationService.createRecommendation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecommendationDTO> updateRecommendation(@PathVariable Long id, @RequestBody RecommendationDTO dto) {
        RecommendationDTO updated = recommendationService.updateRecommendation(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long id) {
        recommendationService.deleteRecommendation(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // MÉTHODES LIÉES À RULE
    // =========================================================

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendationsByRuleId(@PathVariable Long ruleId) {
        List<RecommendationDTO> dtos = recommendationService.getRecommendationsByRuleId(ruleId);
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/rule/{ruleId}")
    public ResponseEntity<Void> deleteRecommendationsByRuleId(@PathVariable Long ruleId) {
        recommendationService.deleteRecommendationsByRuleId(ruleId);
        return ResponseEntity.noContent().build();
    }
}
