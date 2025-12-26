package fr._il.MedFall.services;

import fr._il.MedFall.DTO.User.UserPublicDto;
import fr._il.MedFall.DTO.User.UserUpdateProfileRequest;
import fr._il.MedFall.entities.User;

public interface IUserService {
    void registerUser(User user);
    UserPublicDto updateProfile(UserUpdateProfileRequest request);
}
