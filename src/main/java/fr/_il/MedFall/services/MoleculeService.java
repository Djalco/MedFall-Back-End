package fr._il.MedFall.services;


import java.util.List;
import java.util.Set;

import fr._il.MedFall.DTO.MoleculeDTO;


public interface MoleculeService {

    // -----------------------------
    // CRUD de Molecule
    // -----------------------------
    List<MoleculeDTO> getAllMolecules();

    MoleculeDTO getMoleculeById(Long id);

    MoleculeDTO getMoleculeByName(String name);

    MoleculeDTO createMolecule(MoleculeDTO moleculeDTO);

    Set<Long> getMedicationClassIdsByMolecule(Long moleculeId); 
    
    MoleculeDTO updateMolecule(Long id, MoleculeDTO moleculeDTO);

    void deleteMolecule(Long id);

    List<MoleculeDTO> searchMolecule(String name);

    // -----------------------------
    // Gestion des MedicationClass liées
    // -----------------------------
    // Suggestion : Retourner l'état mis à jour de la molécule
    MoleculeDTO addMedicationClassToMolecule(Long moleculeId, Long classId);

    // Suggestion : Retourner l'état mis à jour de la molécule
    MoleculeDTO removeMedicationClassFromMolecule(Long moleculeId, Long classId);

    List<MoleculeDTO> getMoleculesByClassId(Long classId);
    
}
