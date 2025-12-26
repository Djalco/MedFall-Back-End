package fr._il.MedFall.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr._il.MedFall.entities.MedicationClass;

@Repository
public interface MedicationClassRepository extends JpaRepository<MedicationClass, Long> {

    Optional<MedicationClass> findByName(String name);
    List<MedicationClass> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
    // Suggestion : Ajoutez existsByNameIgnoreCase(String name) pour la cohérence

    // Récupère une seule classe par ID en chargeant ses relations
    @Query("""
        SELECT mc FROM MedicationClass mc 
        LEFT JOIN FETCH mc.molecules m 
        LEFT JOIN FETCH mc.condition c
        WHERE mc.id = :id
    """)
    Optional<MedicationClass> findByIdWithEagerCollections(@Param("id") Long id);
    // Cette méthode manquait et est essentielle pour une récupération optimisée d'un élément unique.


    // Récupère les classes sans condition associée (One-to-One)
    @Query("""
        SELECT mc FROM MedicationClass mc 
        WHERE mc.id NOT IN (
            SELECT c.medicationClass.id 
            FROM ClassCountCondition c 
            WHERE c.medicationClass IS NOT NULL
        )
    """)
    List<MedicationClass> findAllUnassigned();

    // Requêtes optimisées (FETCH JOIN)
    // ----------------------------------------------------------------------------------

    /**
     * Charge toutes les classes de médicaments en récupérant immédiatement (Eagerly)
     * les collections de molécules (Many-to-Many) et la condition (One-to-One)
     * en utilisant un seul LEFT JOIN. Utilisé pour éviter les requêtes N+1.
     */
    @Query("""
        SELECT mc FROM MedicationClass mc 
        LEFT JOIN FETCH mc.molecules m 
        LEFT JOIN FETCH mc.condition c
    """)
    List<MedicationClass> findAllWithEagerCollections();


    /**
     * Recherche partielle et insensible à la casse, en chargeant immédiatement 
     * les collections de molécules et la condition.
     */
    @Query("""
        SELECT mc FROM MedicationClass mc 
        LEFT JOIN FETCH mc.molecules m 
        LEFT JOIN FETCH mc.condition c
        WHERE LOWER(mc.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<MedicationClass> findByNameContainingIgnoreCaseWithEagerCollections(@Param("name") String name);
}