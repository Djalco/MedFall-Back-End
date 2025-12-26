package fr._il.MedFall.controllers;

import fr._il.MedFall.entities.User;
import fr._il.MedFall.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final IUserService userService;

    @PostMapping("add-new-user")
    public ResponseEntity<Map<String, String>> addNewUser(@RequestBody User user) {
        this.userService.registerUser(user);
        return ResponseEntity.ok(Map.of("status", "success", "message", "New user added successfully"));
    }


}
