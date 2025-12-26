package fr._il.MedFall.services;

import fr._il.MedFall.DTO.TargetDTO;
import fr._il.MedFall.enums.TargetType;

import java.util.List;

public interface TargetService {
    List<TargetDTO> getAllTargets();
    TargetDTO getTargetById(Long id);
    TargetDTO getTargetByName(String name);
    TargetDTO createTarget(TargetDTO targetDTO);
    TargetDTO updateTarget(Long id, TargetDTO targetDTO);
    void deleteTarget(Long id);
    List<TargetDTO> searchTargets(String name);
    List<TargetDTO> getTargetsByType(TargetType type);
}