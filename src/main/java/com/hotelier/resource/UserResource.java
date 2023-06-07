package com.hotelier.resource;


import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.UserDto;
import com.hotelier.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.BindException;
import java.util.List;
import java.util.logging.Logger;


@RestController @RequiredArgsConstructor
@RequestMapping("api/v1/user") @Transactional(rollbackFor = {Hotelier.class, NullPointerException.class, Exception.class, BindException.class, Constants.ConstantException.class})
@Tag(name="User", description = "user endpoints")
public class UserResource {
    private final UserService userService;
    Logger log = Logger.getLogger(this.getClass().getName());

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addOrEditUser(@Valid @RequestBody UserDto request) {
        userService.addOrEditUser(request);
    }
    @GetMapping
    public List<UserDto> findAllUser(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id){
        return userService.getUserDto(id);
    }

    @GetMapping ("email/{email}")
    public UserDto findBusinessByAppId(@PathVariable("email") String emailAddress){
        return userService.getUserDtoByEmailAddress(emailAddress);
    }

}
