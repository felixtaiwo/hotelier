package com.hotelier.model.dto;

import com.hotelier.model.entity.Role;
import com.hotelier.model.entity.User;
import com.hotelier.model.enums.SystemRoles;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    Long id;
    @NotNull @Size(min = 2)
    String firstname;
    @NotNull @Size(min = 2)
    String lastname;
    @Email
    String emailAddress;
    @Size(min = 6, message = "password character must be greater than 6 in length")
    String password;
    @NotNull @Size(min = 8)
    String phoneNumber;
    SystemRoles[] permissions;

    public static User toUser(UserDto userDto, PasswordEncoder encoder) {
        User user = new User();
        user.setId(userDto.id);
        user.setFirstname(userDto.firstname);
        user.setEmailAddress(userDto.emailAddress);
        user.setLastname(userDto.lastname);
        user.setMobile(userDto.phoneNumber);
        user.setPassword(userDto.password == null ? "" : encoder.encode(userDto.password));
        return user;
    }

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstname(user.getFirstname());
        userDto.setEmailAddress(user.getEmailAddress());
        userDto.setLastname(user.getLastname());
        userDto.setPhoneNumber(user.getMobile());
        userDto.setPermissions(getPermissions(user.getRoles()));
        return userDto;
    }

    private static SystemRoles[] getPermissions(Set<Role> roles) {
        List<SystemRoles> collect = roles.stream().map(Role::getTitle).collect(Collectors.toList());
        SystemRoles[] result = new SystemRoles[collect.size()];
        collect.toArray(result);
        return result;
    }
}
