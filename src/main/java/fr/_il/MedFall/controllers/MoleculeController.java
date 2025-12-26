package fr._il.MedFall.controllers;

import java.util.List;
import java.util.Set; // Ajout nécessaire pour le type Set<Long>

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

import fr._il.MedFall.DTO.MoleculeDTO;
import fr._il.MedFall.services.MoleculeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("molecules")
@RequiredArgsConstructor
public class MoleculeController {

    private final MoleculeService moleculeService;

    // =========================================================
    // CRUD BASIQUE (Lecture & Recherche)
    // =========================================================
    @GetMapping
    public ResponseEntity<List<MoleculeDTO>> getAllMolecules() {
        return ResponseEntity.ok(moleculeService.getAllMolecules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoleculeDTO> getMoleculeById(@PathVariable Long id) {
        return ResponseEntity.ok(moleculeService.getMoleculeById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<MoleculeDTO> getMoleculeByName(@PathVariable String name) {
        return ResponseEntity.ok(moleculeService.getMoleculeByName(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MoleculeDTO>> searchMolecules(@RequestParam String name) {
        return ResponseEntity.ok(moleculeService.searchMolecule(name));
    }

    // =========================================================
    // CRUD BASIQUE (Création & Mise à jour)
    // =========================================================
    @PostMapping
    public ResponseEntity<MoleculeDTO> createMolecule(@RequestBody MoleculeDTO moleculeDTO) {
        MoleculeDTO created = moleculeService.createMolecule(moleculeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoleculeDTO> updateMolecule(@PathVariable Long id,
            @RequestBody MoleculeDTO moleculeDTO) {

        return ResponseEntity.ok(moleculeService.updateMolecule(id, moleculeDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMolecule(@PathVariable Long id) {
        moleculeService.deleteMolecule(id);
    }

    // =========================================================
    // RELATION MANY-TO-MANY (Lecture & Opérations directes)
    // =========================================================
    @GetMapping("/{moleculeId}/classes")
    // CORRECTION : Attend un Set<Long> pour respecter la sémantique de M:M dans le service
    public ResponseEntity<Set<Long>> getMedicationClassesByMolecule(@PathVariable Long moleculeId) {
        Set<Long> classIds = moleculeService.getMedicationClassIdsByMolecule(moleculeId);
        return ResponseEntity.ok(classIds);
    }
    

    @PostMapping("/{moleculeId}/classes/{classId}")
    // CORRECTION : Retourne l'entité mise à jour (200 OK)
    public ResponseEntity<MoleculeDTO> addMedicationClassToMolecule(@PathVariable Long moleculeId, @PathVariable Long classId) {
        MoleculeDTO updatedMolecule = moleculeService.addMedicationClassToMolecule(moleculeId, classId);
        return ResponseEntity.ok(updatedMolecule);
    }

    @DeleteMapping("/{moleculeId}/classes/{classId}")
    // CORRECTION : Retourne l'entité mise à jour (200 OK)
    public ResponseEntity<MoleculeDTO> removeMedicationClassFromMolecule(@PathVariable Long moleculeId, @PathVariable Long classId) {
        MoleculeDTO updatedMolecule = moleculeService.removeMedicationClassFromMolecule(moleculeId, classId);
        return ResponseEntity.ok(updatedMolecule);
    }
}
