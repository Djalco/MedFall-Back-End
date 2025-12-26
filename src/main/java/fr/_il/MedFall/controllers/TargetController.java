package fr._il.MedFall.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import fr._il.MedFall.DTO.TargetDTO;
import fr._il.MedFall.enums.TargetType;
import fr._il.MedFall.services.TargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("targets")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class TargetController {

    private final TargetService targetService;

    @GetMapping
    public ResponseEntity<List<TargetDTO>> getAllTargets() {
        List<TargetDTO> targets = targetService.getAllTargets();
        return ResponseEntity.ok(targets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TargetDTO> getTargetById(@PathVariable Long id) {
        TargetDTO target = targetService.getTargetById(id);
        return ResponseEntity.ok(target);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TargetDTO> getTargetByName(@PathVariable String name) {
        TargetDTO target = targetService.getTargetByName(name);
        return ResponseEntity.ok(target);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TargetDTO>> getTargetsByType(@PathVariable TargetType type) {
        List<TargetDTO> targets = targetService.getTargetsByType(type);
        return ResponseEntity.ok(targets);
    }

    @PostMapping
    public ResponseEntity<TargetDTO> createTarget(@RequestBody TargetDTO targetDTO) {
        TargetDTO createdTarget = targetService.createTarget(targetDTO);
        return new ResponseEntity<>(createdTarget, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TargetDTO> updateTarget(@PathVariable Long id, @RequestBody TargetDTO targetDTO) {
        TargetDTO updatedTarget = targetService.updateTarget(id, targetDTO);
        return ResponseEntity.ok(updatedTarget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarget(@PathVariable Long id) {
        targetService.deleteTarget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TargetDTO>> searchTargets(@RequestParam String name) {
        List<TargetDTO> targets = targetService.searchTargets(name);
        return ResponseEntity.ok(targets);
    }
}
