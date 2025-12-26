package fr._il.MedFall.DTO.User;

import fr._il.MedFall.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserUpdateProfileRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private String address;
    private Role role;
}
