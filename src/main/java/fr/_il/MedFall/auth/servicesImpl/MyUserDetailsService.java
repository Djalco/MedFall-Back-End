package fr._il.MedFall.auth.servicesImpl;

import fr._il.MedFall.auth.UserPrincipal;
import fr._il.MedFall.entities.User;
import fr._il.MedFall.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailEquals(email).orElseThrow(
                () -> new UsernameNotFoundException("L'utilisateur n'existe pas.")
        );

        return new UserPrincipal(user);
    }
}
