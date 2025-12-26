package fr._il.MedFall.services;

import fr._il.MedFall.DTO.PathologyDTO;

import java.util.List;

public interface PathologyService {
    List<PathologyDTO> getAllPathologies();
    PathologyDTO getPathologyById(Long id);
    PathologyDTO getPathologyByName(String name);
    PathologyDTO createPathology(PathologyDTO pathologyDTO);
    PathologyDTO updatePathology(Long id, PathologyDTO pathologyDTO);
    void deletePathology(Long id);
    List<PathologyDTO> searchPathologies(String name);

    // Nouvelles méthodes pour la gestion des relations avec Target
    List<PathologyDTO> getPathologiesByTarget(Long targetId);
    List<PathologyDTO> getPathologiesWithoutTarget();
    PathologyDTO assignTargetToPathology(Long pathologyId, Long targetId);
    PathologyDTO removeTargetFromPathology(Long pathologyId);
}
