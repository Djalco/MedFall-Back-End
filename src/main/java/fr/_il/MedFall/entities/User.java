package fr._il.MedFall.entities;

import fr._il.MedFall.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer userId;

    String firstName;
    String lastName;
    @Column(unique = true)
    String email;
    @Column(unique = true)
    String phoneNumber;
    String address;
    LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    Role role;
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, avec au moins un chiffre, une lettre majuscule et un caractère spécial."
    )
    String password;
    String resetPwdCode;
    LocalDateTime resetPwdDate;

    String emailCode;
    LocalDateTime emailCodeDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;

    boolean isLocked;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
