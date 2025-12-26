package fr._il.MedFall.repositories;

import fr._il.MedFall.entities.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    // Récupérer toutes les recommandations liées à une règle spécifique
    List<Recommendation> findByRule_Id(Long ruleId);

    // Supprimer toutes les recommandations liées à une règle
    void deleteByRule_Id(Long ruleId);

    // Vérifier si une recommandation existe pour une règle spécifique
    boolean existsByRule_Id(Long ruleId);
}
