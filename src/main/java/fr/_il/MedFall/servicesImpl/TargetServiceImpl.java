package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.DTO.PathologyDTO;
import fr._il.MedFall.DTO.TargetDTO;
import fr._il.MedFall.entities.Target;
import fr._il.MedFall.enums.TargetType;
import fr._il.MedFall.repositories.TargetRepository;
import fr._il.MedFall.services.TargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TargetServiceImpl implements TargetService {

    private final TargetRepository targetRepository;

    @Override
    public List<TargetDTO> getAllTargets() {
        return targetRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public TargetDTO getTargetById(Long id) {
        Target target = targetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target not found with id: " + id));
        return convertToDTO(target);
    }

    @Override
    public TargetDTO getTargetByName(String name) {
        Target target = targetRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Target not found with name: " + name));
        return convertToDTO(target);
    }

    @Override
    public TargetDTO createTarget(TargetDTO targetDTO) {
        if (targetRepository.existsByName(targetDTO.getName())) {
            throw new RuntimeException("Target with name '" + targetDTO.getName() + "' already exists");
        }

        Target target = convertToEntity(targetDTO);
        Target savedTarget = targetRepository.save(target);
        return convertToDTO(savedTarget);
    }

    @Override
    public TargetDTO updateTarget(Long id, TargetDTO targetDTO) {
        Target existingTarget = targetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target not found with id: " + id));

        if (!existingTarget.getName().equals(targetDTO.getName()) &&
                targetRepository.existsByName(targetDTO.getName())) {
            throw new RuntimeException("Target with name '" + targetDTO.getName() + "' already exists");
        }

        existingTarget.setName(targetDTO.getName());
        existingTarget.setType(targetDTO.getType());

        Target updatedTarget = targetRepository.save(existingTarget);
        return convertToDTO(updatedTarget);
    }

    @Override
    public void deleteTarget(Long id) {
        if (!targetRepository.existsById(id)) {
            throw new RuntimeException("Target not found with id: " + id);
        }
        targetRepository.deleteById(id);
    }

    @Override
    public List<TargetDTO> searchTargets(String name) {
        return targetRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<TargetDTO> getTargetsByType(TargetType type) {
        return targetRepository.findByType(type)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private TargetDTO convertToDTO(Target target) {
        List<PathologyDTO> pathologyDTOs = target.getPathologies().stream()
                .map(pathology -> PathologyDTO.builder()
                        .id(pathology.getId())
                        .name(pathology.getName())
                        .description(pathology.getDescription())
                        .build())
                .toList();

        return TargetDTO.builder()
                .id(target.getId())
                .name(target.getName())
                .type(target.getType())
                .pathologies(pathologyDTOs)
                .build();
    }

    private Target convertToEntity(TargetDTO dto) {
        return Target.builder()
                .name(dto.getName())
                .type(dto.getType())
                .pathologies(new ArrayList<>())
                .build();
    }
}