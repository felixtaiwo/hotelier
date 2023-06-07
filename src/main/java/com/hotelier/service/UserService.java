package com.hotelier.service;

import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.UserDto;
import com.hotelier.model.entity.User;
import com.hotelier.model.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;

    public void addOrEditUser(UserDto userDto) {
        User user = UserDto.toUser(userDto, encoder);
        if (user.getId() == null) {
            if(!userRepo.existsByEmailAddress(userDto.getEmailAddress())){
                userRepo.save(user);
            } else
                throw new Hotelier(HttpStatus.BAD_REQUEST, "User already exists with a similar cred");
        } else {
            getUser(userDto.getId());
            userRepo.save(user);
        }
    }
    public void getUser(Long id){
        if(id==null){
            throw new Hotelier(HttpStatus.BAD_REQUEST, "user id cannot be null");
        }
        userRepo.findById(id).orElseThrow(
                () -> new Hotelier(HttpStatus.NOT_FOUND,
                        String.format("User with id %s does not exist", id))
        );
    }
    public UserDto getUserDto(Long id){
        User user  = userRepo.findById(id).orElseThrow(
                () -> new Hotelier(HttpStatus.NOT_FOUND,
                        String.format("User with id %s does not exist", id))
        );
        return UserDto.fromUser(user);
    }

    public User getUserByEmailAddress(String emailAddress){
        return userRepo.findByEmailAddress(emailAddress).orElseThrow(
                () -> new Hotelier(HttpStatus.NOT_FOUND,
                        String.format("User with email address %s does not exist", emailAddress))
        );
    }
    public UserDto getUserDtoByEmailAddress(String emailAddress){
        User user =  userRepo.findByEmailAddress(emailAddress).orElseThrow(
                () -> new Hotelier(HttpStatus.NOT_FOUND,
                        String.format("User with email address %s does not exist", emailAddress))
        );
        return UserDto.fromUser(user);
    }


    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream().map(UserDto::fromUser).collect(Collectors.toList());
    }
}
