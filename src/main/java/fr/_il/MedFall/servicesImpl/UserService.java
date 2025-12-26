package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.DTO.User.UserPublicDto;
import fr._il.MedFall.DTO.User.UserUpdateProfileRequest;
import fr._il.MedFall.entities.User;
import fr._il.MedFall.exceptions.User.DuplicateFieldException;
import fr._il.MedFall.repositories.UserRepository;
import fr._il.MedFall.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateFieldException("Cet email est déjà utilisé.");
        }
        if(userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new DuplicateFieldException("Ce numéro de téléphone est déjà utilisé.");
        }

        user.setPassword(passwordEncoder.encode(user.getPhoneNumber()));
        userRepository.save(user);
    }

    @Override
    public UserPublicDto updateProfile(UserUpdateProfileRequest request) {
        Optional<User> optional = userRepository.findByEmailEquals(request.getEmail());
        if(optional.isPresent()) {
            User user = optional.get();
            updateUserDetails(user, request);

            return new UserPublicDto(userRepository.save(user));
        }
        return null;
    }

    /**
     * UTIL METHODS
     **/
    private void updateUserDetails(User user, UserUpdateProfileRequest request) {

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getPhoneNumber() != null) {
            if(userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DuplicateFieldException("Ce numéro de téléphone est déjà utilisé.");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
    }
}
