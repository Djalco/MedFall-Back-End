package fr._il.MedFall.servicesImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr._il.MedFall.DTO.ClassCountConditionDTO;
import fr._il.MedFall.DTO.MedicationClassDTO;
import fr._il.MedFall.DTO.MoleculeDTO;
import fr._il.MedFall.entities.ClassCountCondition;
import fr._il.MedFall.entities.MedicationClass;
import fr._il.MedFall.entities.Molecule;
import fr._il.MedFall.repositories.ClassCountConditionRepository;
import fr._il.MedFall.repositories.MedicationClassRepository;
import fr._il.MedFall.repositories.MoleculeRepository;
import fr._il.MedFall.services.MedicationClassService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor; 

@Service
@Transactional
@RequiredArgsConstructor
public class MedicationClassServiceImpl implements MedicationClassService {

    private final MedicationClassRepository medicationClassRepository;
    private final MoleculeRepository moleculeRepository;
    private final ClassCountConditionRepository classCountConditionRepository;

    // ============================================================
    // CRÉATION ET MISE À JOUR (C/U)
    // ============================================================

    @Override
    public MedicationClassDTO createMedicationClass(MedicationClassDTO dto) {
        if (medicationClassRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Une classe avec ce nom existe déjà");
        }

        MedicationClass newClass = new MedicationClass();
        newClass.setName(dto.getName());
        newClass.setMolecules(new HashSet<>()); 

        MedicationClass saved = medicationClassRepository.save(newClass);
        
        // Si la DTO contenait des IDs de molécules à la création, on les gère ici:
        if (dto.getMoleculeIds() != null && !dto.getMoleculeIds().isEmpty()) {
            updateMoleculeLinks(saved, List.copyOf(dto.getMoleculeIds()));
        }

        return toDTO(saved);
    }

    @Override
    public MedicationClassDTO updateMedicationClass(Long id, MedicationClassDTO dto) {

        MedicationClass mc = medicationClassRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + id + "]"));

        // 1. Mise à jour du nom (et validation d'unicité si le nom change)
        if (!mc.getName().equalsIgnoreCase(dto.getName()) && medicationClassRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Le nom de la classe '" + dto.getName() + "' est déjà utilisé.");
        }
        mc.setName(dto.getName());

        // 2. Gestion des liens : Appel de la méthode interne de mise à jour des liens
        updateMoleculeLinks(mc, List.copyOf(dto.getMoleculeIds()));

        // 3. Sauvegarde de MedicationClass (pour les champs propres comme le nom)
        return toDTO(medicationClassRepository.save(mc));
    }

    // ============================================================
    // LIENS MANY-TO-MANY (Gestion des Set<Molecule>)
    // ============================================================

    /**
     * Synchronise les liens M:M. Sauvegarde les entités **Molecule** (le côté PROPRIÉTAIRE JPA).
     */
    private void updateMoleculeLinks(MedicationClass mc, List<Long> desiredMoleculeIds) {
        // Optimisation : Conversion en Set pour une recherche O(1)
        Set<Long> desiredIdsSet = desiredMoleculeIds != null 
            ? new HashSet<>(desiredMoleculeIds) 
            : Collections.emptySet();
        
        Set<Molecule> moleculesToSave = new HashSet<>();
        Set<Molecule> currentMolecules = mc.getMolecules();

        // --- 1. Molécules à SUPPRIMER (Rupture des liens existants) ---
        Set<Molecule> moleculesToRemove = currentMolecules.stream()
            // Utilisation du Set pour le .contains()
            .filter(molecule -> !desiredIdsSet.contains(molecule.getId()))
            .collect(Collectors.toSet());
        
        moleculesToRemove.forEach(molecule -> {
            // Rupture bidirectionnelle: Côté INVERSE (MedicationClass)
            mc.getMolecules().remove(molecule);
            // Rupture bidirectionnelle: Côté PROPRIÉTAIRE (Molecule)
            molecule.getMedicationClasses().remove(mc);
            moleculesToSave.add(molecule);
        });

        // --- 2. Molécules à AJOUTER (Établissement des nouveaux liens) ---
        Set<Long> currentIdsSet = currentMolecules.stream().map(Molecule::getId).collect(Collectors.toSet());
        
        Set<Long> idsToAdd = desiredIdsSet.stream() 
            .filter(desiredId -> !currentIdsSet.contains(desiredId))
            .collect(Collectors.toSet()); // Utilisation d'un Set

        if (!idsToAdd.isEmpty()) {
            List<Molecule> moleculesToAdd = moleculeRepository.findAllById(idsToAdd);
            
            if (moleculesToAdd.size() != idsToAdd.size()) {
                throw new EntityNotFoundException("Une ou plusieurs molécules à ajouter n'ont pas été trouvées.");
            }

            moleculesToAdd.forEach(molecule -> {
                // Liaison bidirectionnelle: Côté INVERSE (MedicationClass)
                mc.getMolecules().add(molecule);
                // Liaison bidirectionnelle: Côté PROPRIÉTAIRE (Molecule)
                if (molecule.getMedicationClasses() == null) {
                    molecule.setMedicationClasses(new HashSet<>());
                }
                molecule.getMedicationClasses().add(mc);
                moleculesToSave.add(molecule);
            });
        }
        
        // --- 3. PERSISTANCE CRUCIALE ---
        // Sauvegarde de toutes les entités Molecule modifiées (le côté PROPRIÉTAIRE).
        moleculeRepository.saveAll(moleculesToSave);
    }

    // CORRIGÉ : Type de retour (MedicationClassDTO)
    @Override
    public MedicationClassDTO addMoleculeToClass(Long classId, Long moleculeId) {
        MedicationClass mc = medicationClassRepository.findById(classId)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + classId + "]"));
        Molecule mol = moleculeRepository.findById(moleculeId)
            .orElseThrow(() -> new NoSuchElementException("Molécule introuvable [ID: " + moleculeId + "]"));

        // Ajout au Set (côté inverse)
        if (mc.getMolecules().add(mol)) {
            mol.getMedicationClasses().add(mc); // Relation inverse (côté PROPRIÉTAIRE)
            moleculeRepository.save(mol); // SAUVEGARDE DU PROPRIÉTAIRE
        }
        return toDTO(mc);
    }

    // CORRIGÉ : Type de retour (MedicationClassDTO)
    @Override
    public MedicationClassDTO removeMoleculeFromClass(Long classId, Long moleculeId) {
        MedicationClass mc = medicationClassRepository.findById(classId)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + classId + "]"));
        Molecule mol = moleculeRepository.findById(moleculeId)
            .orElseThrow(() -> new NoSuchElementException("Molécule introuvable [ID: " + moleculeId + "]"));

        // Suppression du Set (côté inverse)
        if (mc.getMolecules().remove(mol)) {
            mol.getMedicationClasses().remove(mc); // Relation inverse (côté PROPRIÉTAIRE)
            moleculeRepository.save(mol); // SAUVEGARDE DU PROPRIÉTAIRE
        }
        return toDTO(mc);
    }
    
    // ============================================================
    // SUPPRESSION (DELETE)
    // ============================================================

    @Override
    public void deleteMedicationClass(Long id) {
        MedicationClass mc = medicationClassRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + id + "]"));

        // 1. Rupture explicite de TOUS les liens du côté PROPRIÉTAIRE (Molecule)
        Set<Molecule> moleculesToUpdate = new HashSet<>();
        
        if (mc.getMolecules() != null) {
            new HashSet<>(mc.getMolecules()).forEach(mol -> {
                mol.getMedicationClasses().remove(mc); // Rupture du lien côté PROPRIÉTAIRE
                moleculesToUpdate.add(mol);
            });
            mc.getMolecules().clear(); 
        }
        
        // 2. Sauvegarder les Molecules modifiées pour nettoyer la table de jointure.
        // C'est essentiel pour garantir que la table M:M est propre avant la suppression de mc.
        moleculeRepository.saveAll(moleculesToUpdate);
        
        // 3. Suppression de la condition One-to-One
        deleteConditionByClassId(id); 
        
        // 4. Suppression de l'entité MedicationClass.
        medicationClassRepository.delete(mc);
    }
    
    // ============================================================
    // CONDITIONS (ClassCountCondition) - MANTENU TEL QUEL
    // ============================================================
    
    @Override
    public ClassCountConditionDTO updateOrCreateCondition(Long classId, ClassCountConditionDTO dto) {
        MedicationClass mc = medicationClassRepository.findById(classId)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable"));

        ClassCountCondition condition = mc.getCondition();
        if (condition == null) {
            condition = toConditionEntity(dto);
            condition.setMedicationClass(mc); 
            mc.setCondition(condition); 
        } else {
            condition.setComparator(dto.getComparator());
            condition.setThreshold(dto.getThreshold());
            condition.setDistinctMolecules(dto.getDistinctMolecules());
        }

        ClassCountCondition savedCondition = classCountConditionRepository.save(condition);
        return toConditionDTO(savedCondition);
    }
    
    @Override
    public void deleteConditionByClassId(Long classId) {
        MedicationClass mc = medicationClassRepository.findById(classId)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable"));

        ClassCountCondition condition = mc.getCondition();
        if (condition != null) {
            mc.setCondition(null);
            medicationClassRepository.save(mc); 

            classCountConditionRepository.delete(condition);
        }
    }
        
    @Override
    @Transactional(readOnly = true)
    public ClassCountConditionDTO getConditionByClassId(Long classId) {
        MedicationClass mc = medicationClassRepository.findById(classId)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable"));
        return mc.getCondition() != null ? toConditionDTO(mc.getCondition()) : null;
    }


    // ============================================================
    // LECTURE (READ) - AJUSTÉ POUR COHÉRENCE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<MedicationClassDTO> getAllMedicationClass() {
        return medicationClassRepository.findAll().stream() 
            .map(this::toDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationClassDTO> getUnassignedMedicationClasses() {
        // Utilisation du filtre en mémoire. Une requête Repository serait plus performante.
        return medicationClassRepository.findAll().stream()
            .filter(mc -> mc.getMolecules() == null || mc.getMolecules().isEmpty())
            .map(this::toDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicationClassDTO getMedicationClassById(Long id) {
        MedicationClass mc = medicationClassRepository.findById(id) // Utiliser findByIdWithEagerCollections si l'Eager Fetching est souhaité
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + id + "]"));
        return toDTO(mc);
    }
    
    // ... Autres méthodes de lecture (get, search) ...

    // CORRIGÉ : Type de retour (Set<MoleculeDTO>)
    @Override
    @Transactional(readOnly = true)
    public List<MoleculeDTO> getMoleculesByClassId(Long classId) {
        MedicationClass mc = medicationClassRepository.findById(classId)
            .orElseThrow(() -> new NoSuchElementException("Classe introuvable [ID: " + classId + "]"));

        return mc.getMolecules().stream()
            .map(this::toMoleculeDTO).toList();
    }


    // ============================================================
    // MAPPERS (Méthodes de conversion) - MANTENU TEL QUEL
    // ============================================================

    private MedicationClassDTO toDTO(MedicationClass entity) {

        // CORRECTION : Utiliser Set<Long> et collecter en Set
        Set<Long> moleculeIds = entity.getMolecules() != null
                ? entity.getMolecules().stream().map(Molecule::getId).collect(Collectors.toSet())
                : Collections.emptySet(); // emptySet() est préférable à emptyList()

        return MedicationClassDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                // Passe le Set<Long> au builder
                .moleculeIds(moleculeIds)
                .moleculeCount(moleculeIds.size())
                .classCountCondition(entity.getCondition() != null
                        ? toConditionDTO(entity.getCondition())
                        : null)
                .build();
    }
    

    private ClassCountCondition toConditionEntity(ClassCountConditionDTO dto) {
        return ClassCountCondition.builder()
            .id(dto.getId())
            .comparator(dto.getComparator())
            .threshold(dto.getThreshold())
            .distinctMolecules(dto.getDistinctMolecules())
            .build();
    }

    private ClassCountConditionDTO toConditionDTO(ClassCountCondition entity) {
        return ClassCountConditionDTO.builder()
            .id(entity.getId())
            .comparator(entity.getComparator())
            .threshold(entity.getThreshold())
            .distinctMolecules(entity.getDistinctMolecules())
            .build();
    }

    private MoleculeDTO toMoleculeDTO(Molecule molecule) {

        // CORRECTION : Utiliser Set<Long> et collecter en Set
        Set<Long> relatedClassIds = molecule.getMedicationClasses() != null
                ? molecule.getMedicationClasses().stream().map(MedicationClass::getId).collect(Collectors.toSet())
                : Collections.emptySet();

        return MoleculeDTO.builder()
                .id(molecule.getId())
                .name(molecule.getName())
                .halfLifeHours(molecule.getHalfLifeHours())
                .standardDurationWeeks(molecule.getStandardDurationWeeks())
                // Passe le Set<Long> au builder
                .medicationClassIds(relatedClassIds)
                .medicationClassCount(relatedClassIds.size())
                .build();
    }

    @Override
    public MedicationClassDTO getMedicationClassByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MedicationClassDTO> searchMedicationClass(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}