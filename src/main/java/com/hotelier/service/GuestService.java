package com.hotelier.service;

import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.GuestDto;
import com.hotelier.model.entity.Guest;
import com.hotelier.model.entity.Role;
import com.hotelier.model.entity.User;
import com.hotelier.model.enums.SystemRoles;
import com.hotelier.model.repository.*;
import com.hotelier.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepo guestRepo;
    private final UserRepo userRepo;
    private final StateRepo stateRepo;
    private final CountryRepo countryRepo;
    private final RoleRepo roleRepo;
    private final JwtUtils jwtUtils;
    private final FileService fileService;

    public boolean isUserAGuest(User user){
        return guestRepo.findByUser(user) != null;
    }

    public void addOrEditGuest(GuestDto guestDto, Long userId) {
        Guest guest = GuestDto.toGuest(guestDto, stateRepo, countryRepo);
        User user = userId == null ? jwtUtils.userContext() : userRepo.findById(userId)
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "User does not exist"));
        if (guestDto.getId() == null) {
            if (isUserAGuest(user)) {
                throw new Hotelier(HttpStatus.BAD_REQUEST, "User is already registered as a guest");
            }

            guest.setUser(user);
            guestRepo.save(guest);
            Role guestRole = roleRepo.findByTitle(SystemRoles.GUEST);
            AdminService.getUserRole(user, guestRole, userRepo);

        }
    }

    public List<GuestDto> getAllGuest() {
        return guestRepo.findAll()
                .stream().map(g->GuestDto.fromGuest(g, fileService)).collect(Collectors.toList());
    }

    @Bean
    @RequestScope
    public Guest getLoggedInGuest() {
        Guest guest = null;
        User user = jwtUtils.userContext();
        if (user != null && user.getRoles().stream().anyMatch(role -> role.getTitle() == SystemRoles.GUEST)) {
            guest = guestRepo.findByUser(user);
        }
         return guest;
    }

    public Guest getGuest(Long guestId) {
        return guestRepo.findById(guestId).orElseThrow(()->new Hotelier(HttpStatus.NOT_FOUND, "Guest does not exist"));
    }
}
