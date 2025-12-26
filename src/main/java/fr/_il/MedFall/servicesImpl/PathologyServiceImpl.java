package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.DTO.PathologyDTO;
import fr._il.MedFall.entities.Pathology;
import fr._il.MedFall.entities.Target;
import fr._il.MedFall.repositories.PathologyRepository;
import fr._il.MedFall.repositories.TargetRepository;
import fr._il.MedFall.services.PathologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PathologyServiceImpl implements PathologyService {

    private final PathologyRepository pathologyRepository;
    private final TargetRepository targetRepository;

    @Override
    public List<PathologyDTO> getAllPathologies() {
        return pathologyRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public PathologyDTO getPathologyById(Long id) {
        Pathology pathology = pathologyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + id));
        return convertToDTO(pathology);
    }

    @Override
    public PathologyDTO getPathologyByName(String name) {
        Pathology pathology = pathologyRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Pathology not found with name: " + name));
        return convertToDTO(pathology);
    }

    @Override
    public PathologyDTO createPathology(PathologyDTO pathologyDTO) {
        if (pathologyRepository.existsByName(pathologyDTO.getName())) {
            throw new RuntimeException("Pathology with name '" + pathologyDTO.getName() + "' already exists");
        }

        Pathology pathology = convertToEntity(pathologyDTO);
        Pathology savedPathology = pathologyRepository.save(pathology);
        return convertToDTO(savedPathology);
    }

    @Override
    public PathologyDTO updatePathology(Long id, PathologyDTO pathologyDTO) {
        Pathology existingPathology = pathologyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + id));

        if (!existingPathology.getName().equals(pathologyDTO.getName()) &&
                pathologyRepository.existsByName(pathologyDTO.getName())) {
            throw new RuntimeException("Pathology with name '" + pathologyDTO.getName() + "' already exists");
        }

        // Mettre à jour la cible si targetId est fourni
        if (pathologyDTO.getTargetId() != null) {
            Target target = targetRepository.findById(pathologyDTO.getTargetId())
                    .orElseThrow(() -> new RuntimeException("Target not found with id: " + pathologyDTO.getTargetId()));
            existingPathology.setTarget(target);
        } else {
            existingPathology.setTarget(null);
        }

        existingPathology.setName(pathologyDTO.getName());
        existingPathology.setDescription(pathologyDTO.getDescription());

        Pathology updatedPathology = pathologyRepository.save(existingPathology);
        return convertToDTO(updatedPathology);
    }

    @Override
    public void deletePathology(Long id) {
        if (!pathologyRepository.existsById(id)) {
            throw new RuntimeException("Pathology not found with id: " + id);
        }
        pathologyRepository.deleteById(id);
    }

    @Override
    public List<PathologyDTO> searchPathologies(String name) {
        return pathologyRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<PathologyDTO> getPathologiesByTarget(Long targetId) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found with id: " + targetId));

        return pathologyRepository.findByTarget(target)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<PathologyDTO> getPathologiesWithoutTarget() {
        return pathologyRepository.findByTargetIsNull()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public PathologyDTO assignTargetToPathology(Long pathologyId, Long targetId) {
        Pathology pathology = pathologyRepository.findById(pathologyId)
                .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + pathologyId));

        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found with id: " + targetId));

        pathology.setTarget(target);
        Pathology updatedPathology = pathologyRepository.save(pathology);
        return convertToDTO(updatedPathology);
    }

    @Override
    public PathologyDTO removeTargetFromPathology(Long pathologyId) {
        Pathology pathology = pathologyRepository.findById(pathologyId)
                .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + pathologyId));

        pathology.setTarget(null);
        Pathology updatedPathology = pathologyRepository.save(pathology);
        return convertToDTO(updatedPathology);
    }

    private PathologyDTO convertToDTO(Pathology pathology) {
        return PathologyDTO.builder()
                .id(pathology.getId())
                .name(pathology.getName())
                .description(pathology.getDescription())
                .targetId(pathology.getTarget() != null ? pathology.getTarget().getId() : null)
                .targetName(pathology.getTarget() != null ? pathology.getTarget().getName() : null)
                .build();
    }

    private Pathology convertToEntity(PathologyDTO dto) {
        Pathology.PathologyBuilder builder = Pathology.builder()
                .name(dto.getName())
                .description(dto.getDescription());

        if (dto.getTargetId() != null) {
            Target target = targetRepository.findById(dto.getTargetId())
                    .orElseThrow(() -> new RuntimeException("Target not found with id: " + dto.getTargetId()));
            builder.target(target);
        }

        return builder.build();
    }
}