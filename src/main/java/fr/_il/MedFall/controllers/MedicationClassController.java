package fr._il.MedFall.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr._il.MedFall.DTO.ClassCountConditionDTO;
import fr._il.MedFall.DTO.MedicationClassDTO;
import fr._il.MedFall.DTO.MoleculeDTO;
import fr._il.MedFall.services.MedicationClassService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("medication-classes")
@RequiredArgsConstructor
public class MedicationClassController {

    private final MedicationClassService medicationClassService;

    // =========================================================
    // CRUD BASIQUE
    // =========================================================
    @GetMapping
    public ResponseEntity<List<MedicationClassDTO>> getAllMedicationClasses() {
        return ResponseEntity.ok(medicationClassService.getAllMedicationClass());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationClassDTO> getMedicationClassById(@PathVariable Long id) {
        return ResponseEntity.ok(medicationClassService.getMedicationClassById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<MedicationClassDTO> getMedicationClassByName(@PathVariable String name) {
        return ResponseEntity.ok(medicationClassService.getMedicationClassByName(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicationClassDTO>> searchMedicationClasses(@RequestParam String name) {
        return ResponseEntity.ok(medicationClassService.searchMedicationClass(name));
    }

    @PostMapping
    public ResponseEntity<MedicationClassDTO> createMedicationClass(@RequestBody MedicationClassDTO medicationClassDTO) {
        MedicationClassDTO createdDto = medicationClassService.createMedicationClass(medicationClassDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicationClassDTO> updateMedicationClass(
            @PathVariable Long id,
            @RequestBody MedicationClassDTO medicationClassDTO) {
        return ResponseEntity.ok(medicationClassService.updateMedicationClass(id, medicationClassDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicationClass(@PathVariable Long id) {
        medicationClassService.deleteMedicationClass(id);
    }

    // =========================================================
    // RELATION MANY-TO-MANY AVEC MOLECULE (Retourne la ressource modifiée)
    // =========================================================
    @GetMapping("/{classId}/molecules")
    public ResponseEntity<List<MoleculeDTO>> getMoleculesByClassId(@PathVariable Long classId) {
        return ResponseEntity.ok(medicationClassService.getMoleculesByClassId(classId));
    }

    @PostMapping("/{classId}/molecules/{moleculeId}")
    // Renvoyons l'entité mise à jour (200 OK) plutôt que 204
    public ResponseEntity<MedicationClassDTO> addMoleculeToClass(@PathVariable Long classId, @PathVariable Long moleculeId) {
        MedicationClassDTO updatedClass = medicationClassService.addMoleculeToClass(classId, moleculeId);
        return ResponseEntity.ok(updatedClass);
    }

    @DeleteMapping("/{classId}/molecules/{moleculeId}")
    // Renvoyons l'entité mise à jour (200 OK) plutôt que 204
    public ResponseEntity<MedicationClassDTO> removeMoleculeFromClass(@PathVariable Long classId, @PathVariable Long moleculeId) {
        MedicationClassDTO updatedClass = medicationClassService.removeMoleculeFromClass(classId, moleculeId);
        return ResponseEntity.ok(updatedClass);
    }

    // =========================================================
    // RELATION ONE-TO-ONE (CLASS COUNT CONDITION)
    // =========================================================
    @GetMapping("/{classId}/condition")
    public ResponseEntity<ClassCountConditionDTO> getConditionByClassId(@PathVariable Long classId) {
        ClassCountConditionDTO condition = medicationClassService.getConditionByClassId(classId);
        // Retourne 204 No Content si le champ optionnel est null
        return (condition != null) ? ResponseEntity.ok(condition) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{classId}/condition")
    public ResponseEntity<ClassCountConditionDTO> updateOrCreateCondition(
            @PathVariable Long classId,
            @RequestBody ClassCountConditionDTO conditionDTO) {
        return ResponseEntity.ok(medicationClassService.updateOrCreateCondition(classId, conditionDTO));
    }

    @DeleteMapping("/{classId}/condition")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConditionByClassId(@PathVariable Long classId) {
        medicationClassService.deleteConditionByClassId(classId);
    }
}
