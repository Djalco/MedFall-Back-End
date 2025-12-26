package fr._il.MedFall.services;

import java.util.List;
import java.util.Set;

import fr._il.MedFall.DTO.ClassCountConditionDTO;
import fr._il.MedFall.DTO.MedicationClassDTO;
import fr._il.MedFall.DTO.MoleculeDTO;


public interface MedicationClassService {

    // -----------------------------
    // CRUD de MedicationClass
    // -----------------------------
    List<MedicationClassDTO> getAllMedicationClass();

    List<MedicationClassDTO> getUnassignedMedicationClasses();

    MedicationClassDTO getMedicationClassById(Long id);

    MedicationClassDTO getMedicationClassByName(String name);

    MedicationClassDTO createMedicationClass(MedicationClassDTO medicationClassDTO);

    MedicationClassDTO updateMedicationClass(Long id, MedicationClassDTO medicationClassDTO);

    void deleteMedicationClass(Long id);

    List<MedicationClassDTO> searchMedicationClass(String name);

    // -----------------------------
    // Gestion des molécules liées (Many-to-Many)
    // -----------------------------
    // Suggestion : Retourner l'état mis à jour de la classe de médicament
    MedicationClassDTO addMoleculeToClass(Long classId, Long moleculeId);

    // Suggestion : Retourner l'état mis à jour de la classe de médicament
    MedicationClassDTO removeMoleculeFromClass(Long classId, Long moleculeId);

    // Suggestion : Utiliser Set pour la cohérence de la collection
    List<MoleculeDTO> getMoleculesByClassId(Long classId);

    // ... (Gestion de la condition ClassCountCondition : Les signatures sont parfaites) ...
    ClassCountConditionDTO getConditionByClassId(Long classId);
    ClassCountConditionDTO updateOrCreateCondition(Long classId, ClassCountConditionDTO conditionDTO);
    void deleteConditionByClassId(Long classId);
}

