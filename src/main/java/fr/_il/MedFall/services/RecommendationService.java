package fr._il.MedFall.services;

import fr._il.MedFall.DTO.RecommendationDTO;

import java.util.List;

public interface RecommendationService {

    // --- CRUD de base ---
    List<RecommendationDTO> getAllRecommendations();
    RecommendationDTO getRecommendationById(Long id);
    RecommendationDTO createRecommendation(RecommendationDTO dto);
    RecommendationDTO updateRecommendation(Long id, RecommendationDTO dto);
    void deleteRecommendation(Long id);

    // --- Méthodes liées à Rule ---
    List<RecommendationDTO> getRecommendationsByRuleId(Long ruleId);
    void deleteRecommendationsByRuleId(Long ruleId);
}
