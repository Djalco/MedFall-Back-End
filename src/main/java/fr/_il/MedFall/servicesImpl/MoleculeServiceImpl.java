package fr._il.MedFall.servicesImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr._il.MedFall.DTO.MoleculeDTO;
import fr._il.MedFall.entities.MedicationClass;
import fr._il.MedFall.entities.Molecule;
import fr._il.MedFall.repositories.MedicationClassRepository;
import fr._il.MedFall.repositories.MoleculeRepository;
import fr._il.MedFall.services.MoleculeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MoleculeServiceImpl implements MoleculeService {

    private final MoleculeRepository moleculeRepository;
    private final MedicationClassRepository medicationClassRepository;

    // ============================================================
    // CREATE
    // ============================================================
    @Override
    public MoleculeDTO createMolecule(MoleculeDTO moleculeDTO) {

        if (moleculeRepository.existsByNameIgnoreCase(moleculeDTO.getName())) {
            throw new IllegalArgumentException("Une molécule avec ce nom existe déjà");
        }

        Molecule newMolecule = convertToEntity(moleculeDTO);
        newMolecule.setMedicationClasses(new HashSet<>());

        Molecule saved = moleculeRepository.save(newMolecule);

        // Si la DTO contenait des IDs de classes de médicaments à la création
        if (moleculeDTO.getMedicationClassIds() != null && !moleculeDTO.getMedicationClassIds().isEmpty()) {
            updateMedicationLinks(saved, List.copyOf(moleculeDTO.getMedicationClassIds()));

        }

        return toDTO(saved);
    }
    
    /**
     * Synchronise les liens M:M. Sauvegarde les entités **MedicationClass** (le côté PROPRIÉTAIRE JPA).
     */
    private void updateMedicationLinks(Molecule molecule, List<Long> desiredMedicationIds) {

        // Optimisation : Conversion en Set pour une recherche O(1)
        Set<Long> desiredIdsSet = desiredMedicationIds != null 
            ? new HashSet<>(desiredMedicationIds) 
            : Collections.emptySet();

        Set<MedicationClass> classesToSave = new HashSet<>();
        Set<MedicationClass> currentMedicationClasses = molecule.getMedicationClasses();

        // --- 1. Classes à SUPPRIMER (Rupture des liens existants) ---
        Set<MedicationClass> classesToRemove = currentMedicationClasses.stream()
            .filter(medicationClass -> !desiredIdsSet.contains(medicationClass.getId()))
            .collect(Collectors.toSet());

        classesToRemove.forEach(medicationClass -> {
            // Rupture bidirectionnelle: Côté INVERSE (Molecule)
            molecule.getMedicationClasses().remove(medicationClass);
            // Rupture bidirectionnelle: Côté PROPRIÉTAIRE (MedicationClass)
            medicationClass.getMolecules().remove(molecule);
            classesToSave.add(medicationClass);
        });

        // --- 2. Classes à AJOUTER (Établissement des nouveaux liens) ---
        Set<Long> currentIds = currentMedicationClasses.stream().map(MedicationClass::getId).collect(Collectors.toSet());
        Set<Long> idsToAdd = desiredIdsSet.stream()
            .filter(desiredId -> !currentIds.contains(desiredId))
            .collect(Collectors.toSet());

        if (!idsToAdd.isEmpty()) {
            List<MedicationClass> classesToAdd = medicationClassRepository.findAllById(idsToAdd);

            if (classesToAdd.size() != idsToAdd.size()) {
                throw new EntityNotFoundException("Une ou plusieurs classes de médicaments à lier n'ont pas été trouvées.");
            }

            classesToAdd.forEach(medicationClass -> {
                // Liaison bidirectionnelle: Côté INVERSE (Molecule)
                molecule.getMedicationClasses().add(medicationClass);
                // Liaison bidirectionnelle: Côté PROPRIÉTAIRE (MedicationClass)
                if (medicationClass.getMolecules() == null) {
                    medicationClass.setMolecules(new HashSet<>());
                }
                medicationClass.getMolecules().add(molecule);
                classesToSave.add(medicationClass);
            });
        }

        // --- 3. PERSISTANCE CRUCIALE ---
        // Sauvegarde de toutes les entités PROPRIÉTAIRES modifiées (MedicationClass).
        medicationClassRepository.saveAll(classesToSave);
    }

    // ============================================================
    // UPDATE
    // ============================================================
    @Override
    public MoleculeDTO updateMolecule(Long id, MoleculeDTO moleculeDTO) {

        Molecule molecule = moleculeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Molécule introuvable [ID: " + id + "]"));

        // 1. Mise à jour du nom (et validation d'unicité si le nom change)
        if (!molecule.getName().equalsIgnoreCase(moleculeDTO.getName()) 
            && moleculeRepository.existsByNameIgnoreCase(moleculeDTO.getName())) {
            throw new IllegalArgumentException("Le nom de la molécule '" + moleculeDTO.getName() + "' est déjà utilisé.");
        }
        molecule.setName(moleculeDTO.getName());

        // 2. Mise à jour des autres champs de Molecule
        molecule.setHalfLifeHours(moleculeDTO.getHalfLifeHours());
        molecule.setStandardDurationWeeks(moleculeDTO.getStandardDurationWeeks());
        
        // ... mettre à jour d'autres attributs propres à Molecule ...

        // 3. Gestion des liens M:M
        updateMedicationLinks(molecule, List.copyOf(moleculeDTO.getMedicationClassIds()));      
        // 4. Sauvegarde de Molecule (pour les champs propres comme le nom/temps de demi-vie)
        return toDTO(moleculeRepository.save(molecule));
    }

    // ============================================================
    // DELETE
    // ============================================================
    @Override
    public void deleteMolecule(Long id) {
        Molecule moleculeToDelete = moleculeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Molécule non trouvée [ID: " + id + "]"));

        Set<MedicationClass> classesToUpdate = new HashSet<>();

        // 1. Rupture explicite de TOUS les liens du côté PROPRIÉTAIRE (MedicationClass)
        if (moleculeToDelete.getMedicationClasses() != null) {
             new HashSet<>(moleculeToDelete.getMedicationClasses()).forEach(mc -> {
                mc.getMolecules().remove(moleculeToDelete); // Rupture du lien côté PROPRIÉTAIRE
                classesToUpdate.add(mc);
            });
            moleculeToDelete.getMedicationClasses().clear();
        }
        
        // 2. Sauvegarder les MedicationClass modifiées pour nettoyer la table de jointure.
        medicationClassRepository.saveAll(classesToUpdate);
        
        // 3. Suppression de l'entité Molecule.
        moleculeRepository.delete(moleculeToDelete);
    }

    // ============================================================
    // CRUD RELATIONNEL (Ajout/Suppression de lien M:M)
    // ============================================================
    @Override
    public MoleculeDTO addMedicationClassToMolecule(Long moleculeId, Long classId) {
        Molecule molecule = moleculeRepository.findById(moleculeId)
                .orElseThrow(() -> new NoSuchElementException("Molécule introuvable [ID: " + moleculeId + "]"));
        MedicationClass mc = medicationClassRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + classId + "]"));

        // Liaison bidirectionnelle
        if (molecule.getMedicationClasses().add(mc)) { // Côté Inverse
            mc.getMolecules().add(molecule); // Côté Propriétaire
            medicationClassRepository.save(mc); // Sauvegarde du PROPRIÉTAIRE
        }
        return toDTO(molecule);
    }

    @Override
    public MoleculeDTO removeMedicationClassFromMolecule(Long moleculeId, Long classId) {
        Molecule molecule = moleculeRepository.findById(moleculeId)
                .orElseThrow(() -> new NoSuchElementException("Molécule introuvable [ID: " + moleculeId + "]"));
        MedicationClass mc = medicationClassRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + classId + "]"));

        // Rupture bidirectionnelle
        if (molecule.getMedicationClasses().remove(mc)) { // Côté Inverse
            mc.getMolecules().remove(molecule); // Côté Propriétaire
            medicationClassRepository.save(mc); // Sauvegarde du PROPRIÉTAIRE
        }
        return toDTO(molecule);
    }

    // ============================================================
    // READ
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public List<MoleculeDTO> getAllMolecules() {
        return moleculeRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MoleculeDTO getMoleculeById(Long id) {
        // CORRECTION N+1 : Utilisation de findByIdWithClasses, ce qui implique une requête optimisée dans le Repository
        Molecule molecule = moleculeRepository.findByIdWithClasses(id) 
                .orElseThrow(() -> new EntityNotFoundException("Molécule introuvable"));

        return toDTO(molecule);
    }

    @Override
    @Transactional(readOnly = true)
    public MoleculeDTO getMoleculeByName(String name) {
        Molecule molecule = moleculeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NoSuchElementException("Molécule non trouvée [Nom: " + name + "]"));
        return toDTO(molecule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoleculeDTO> searchMolecule(String name) {
        return moleculeRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDTO).toList();
    }

    // Lectures relation M:M (CORRIGÉ : Set<Long>)
    @Override
    @Transactional(readOnly = true)
    public Set<Long> getMedicationClassIdsByMolecule(Long moleculeId) {
        Molecule molecule = moleculeRepository.findById(moleculeId)
                .orElseThrow(() -> new NoSuchElementException("Molécule non trouvée [ID: " + moleculeId + "]"));

        return molecule.getMedicationClasses().stream().map(MedicationClass::getId).collect(Collectors.toSet());
    }

    // NOTE : La méthode List<MoleculeDTO> getMoleculesByClassId(Long classId) a été retirée
    // car elle appartient à MedicationClassService pour respecter le principe SRP.

    // ============================================================
    // MAPPERS (Méthodes de conversion)
    // ============================================================
private MoleculeDTO toDTO(Molecule molecule) {

        Set<Long> classIdsSet = molecule.getMedicationClasses() != null
                ? molecule.getMedicationClasses().stream().map(MedicationClass::getId).collect(Collectors.toSet())
                : Collections.emptySet();

        // Supprimez les lignes inutiles List<Long> classIdsList = ...
        // car le DTO attend un Set<Long> (type correct pour M:M)
        // Assurez-vous d'avoir les autres mappings ici
        return MoleculeDTO.builder()
                .id(molecule.getId())
                .name(molecule.getName())
                .halfLifeHours(molecule.getHalfLifeHours())
                .standardDurationWeeks(molecule.getStandardDurationWeeks())
                // CORRECTION : Utilisez classIdsSet qui est de type Set<Long>
                .medicationClassIds(classIdsSet)
                // CORRECTION : Utilisez la taille du Set
                .medicationClassCount(classIdsSet.size())
                .build();
    }
    private Molecule convertToEntity(MoleculeDTO dto) {
        return Molecule.builder()
            .id(dto.getId())
            .name(dto.getName())
            .halfLifeHours(dto.getHalfLifeHours())
            .standardDurationWeeks(dto.getStandardDurationWeeks())
            .medicationClasses(new HashSet<>()) 
            .build();
    }

    @Override
    public List<MoleculeDTO> getMoleculesByClassId(Long classId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}