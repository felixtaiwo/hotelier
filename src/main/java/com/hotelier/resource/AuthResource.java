package com.hotelier.resource;


import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.LoginDto;
import com.hotelier.model.dto.TokenDto;
import com.hotelier.model.dto.UserDto;
import com.hotelier.security.JwtUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Constants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.BindException;
import java.util.logging.Logger;


@RestController @RequiredArgsConstructor
@RequestMapping("api/v1/auth") @Transactional(rollbackFor = {Hotelier.class, NullPointerException.class, Exception.class, BindException.class, Constants.ConstantException.class})
@Tag(name="Authentication", description = "auth endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AuthResource {
    private final JwtUtils jwt;
    Logger log = Logger.getLogger(this.getClass().getName());

    @PostMapping
    public TokenDto login (@RequestBody @Valid LoginDto loginDto){
        return jwt.login(loginDto);
    }
    @GetMapping("refresh") @RolesAllowed(value = {"GUEST","ADMIN","SUPER_ADMIN"})
    public TokenDto refreshToken (@RequestParam String token){
        return jwt.refreshToken(token);
    }
    @GetMapping("user")
    @RolesAllowed(value = {"GUEST","ADMIN","SUPER_ADMIN"})
    public UserDto currentlyLoggedInUser(@RequestParam String token){
        return jwt.getUserByToken(token);
    }


}
