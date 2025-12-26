package fr._il.MedFall.DTO.User;

import fr._il.MedFall.entities.User;
import fr._il.MedFall.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPublicDto implements Serializable {

    Integer userId;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String address;
    LocalDate birthDate;
    Role role;
    boolean isLocked;

    public UserPublicDto(User user) {
        this.userId = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.birthDate = user.getBirthDate();
        this.role = user.getRole();
        this.isLocked = user.isLocked();
    }
}
