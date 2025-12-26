package fr._il.MedFall.controllers;

import fr._il.MedFall.DTO.PathologyDTO;
import fr._il.MedFall.services.PathologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pathologies")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class PathologyController {

    private final PathologyService pathologyService;

    @GetMapping
    public ResponseEntity<List<PathologyDTO>> getAllPathologies() {
        List<PathologyDTO> pathologies = pathologyService.getAllPathologies();
        return ResponseEntity.ok(pathologies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PathologyDTO> getPathologyById(@PathVariable Long id) {
        PathologyDTO pathology = pathologyService.getPathologyById(id);
        return ResponseEntity.ok(pathology);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PathologyDTO> getPathologyByName(@PathVariable String name) {
        PathologyDTO pathology = pathologyService.getPathologyByName(name);
        return ResponseEntity.ok(pathology);
    }

    @PostMapping
    public ResponseEntity<PathologyDTO> createPathology(@RequestBody PathologyDTO pathologyDTO) {
        PathologyDTO createdPathology = pathologyService.createPathology(pathologyDTO);
        return new ResponseEntity<>(createdPathology, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PathologyDTO> updatePathology(@PathVariable Long id, @RequestBody PathologyDTO pathologyDTO) {
        PathologyDTO updatedPathology = pathologyService.updatePathology(id, pathologyDTO);
        return ResponseEntity.ok(updatedPathology);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePathology(@PathVariable Long id) {
        pathologyService.deletePathology(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PathologyDTO>> searchPathologies(@RequestParam String name) {
        List<PathologyDTO> pathologies = pathologyService.searchPathologies(name);
        return ResponseEntity.ok(pathologies);
    }

    // Nouvelles endpoints pour la gestion des relations avec Target
    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<PathologyDTO>> getPathologiesByTarget(@PathVariable Long targetId) {
        List<PathologyDTO> pathologies = pathologyService.getPathologiesByTarget(targetId);
        return ResponseEntity.ok(pathologies);
    }

    @GetMapping("/without-target")
    public ResponseEntity<List<PathologyDTO>> getPathologiesWithoutTarget() {
        List<PathologyDTO> pathologies = pathologyService.getPathologiesWithoutTarget();
        return ResponseEntity.ok(pathologies);
    }

    @PostMapping("/{pathologyId}/assign-target/{targetId}")
    public ResponseEntity<PathologyDTO> assignTargetToPathology(
            @PathVariable Long pathologyId,
            @PathVariable Long targetId) {
        PathologyDTO updatedPathology = pathologyService.assignTargetToPathology(pathologyId, targetId);
        return ResponseEntity.ok(updatedPathology);
    }

    @PostMapping("/{pathologyId}/remove-target")
    public ResponseEntity<PathologyDTO> removeTargetFromPathology(@PathVariable Long pathologyId) {
        PathologyDTO updatedPathology = pathologyService.removeTargetFromPathology(pathologyId);
        return ResponseEntity.ok(updatedPathology);
    }
}
