package fr._il.MedFall.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; // Ajout pour la cohérence avec le retour des collections
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr._il.MedFall.entities.Molecule;
// import fr._il.MedFall.entities.MedicationClass; // (Pas nécessaire ici)

@Repository
public interface MoleculeRepository extends JpaRepository<Molecule, Long> {

    // Recherche de molécules par nom (partiel, insensible à la casse)
    List<Molecule> findByNameContainingIgnoreCase(String name);

    // Vérifie si une molécule existe déjà par nom (insensible à la casse)
    boolean existsByNameIgnoreCase(String name);
    // Supprime existsByName(String nameMolecule) et findByName(String nameMolecule) 
    // qui sont redondantes avec la recherche insensible à la casse ou mal nommées.

    // Récupère une molécule exacte par nom (insensible à la casse)
    Optional<Molecule> findByNameIgnoreCase(String name); // Ajout pour la cohérence

    // Requête optimisée pour charger les collections Many-to-Many
    // Charge immédiatement (Eagerly) les classes de médicaments associées 
    // et la condition (One-to-One) pour éviter la N+1.
    @Query("""
        SELECT m FROM Molecule m 
        LEFT JOIN FETCH m.medicationClasses mc 
        LEFT JOIN FETCH m.attributeCondition ac
        WHERE m.id = :id
    """)
    Optional<Molecule> findByIdWithClassesAndCondition(@Param("id") Long id); 
    // Renommé findByIdWithClasses pour être plus explicite sur les jointures.
    
    // Si vous avez besoin de récupérer toutes les molécules avec leurs classes chargées
    @Query("""
        SELECT m FROM Molecule m 
        LEFT JOIN FETCH m.medicationClasses mc
    """)
    List<Molecule> findAllWithMedicationClasses();

// Requête optimisée pour charger la collection de MedicationClass en même temps que la Molécule.
    @Query("SELECT m FROM Molecule m LEFT JOIN FETCH m.medicationClasses mc WHERE m.id = :id")
    Optional<Molecule> findByIdWithClasses(@Param("id") Long id);
}