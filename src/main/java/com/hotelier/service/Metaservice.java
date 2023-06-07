package com.hotelier.service;

import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.StateDto;
import com.hotelier.model.entity.Role;
import com.hotelier.model.entity.State;
import com.hotelier.model.enums.SystemRoles;
import com.hotelier.model.repository.CountryRepo;
import com.hotelier.model.repository.RoleRepo;
import com.hotelier.model.repository.StateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Metaservice {
    private final CountryRepo countryRepo;
    private final StateRepo stateRepo;
    private final RoleRepo roleRepo;
    public List<StateDto.CountryDto> getSupportedCountry(){
        return countryRepo.findAll().stream().map(r->StateDto.CountryDto.fromCountry(r,true)).collect(Collectors.toList());
    }

    public List<StateDto.CountryDto> createCountry(StateDto.CountryDto[] countryDtos) {
        return countryRepo.saveAll(Arrays.stream(countryDtos).map(StateDto.CountryDto::toCountry).collect(Collectors.toList()))
                .stream().map(r->StateDto.CountryDto.fromCountry(r,false)).collect(Collectors.toList());

    }

    public List<StateDto> createState(StateDto[] stateDtos, Long countryId) {
        return stateRepo.saveAll(Arrays.stream(stateDtos).map(
                stateDto->{
                    State state = StateDto.toState(stateDto);
                    state.setCountry(countryRepo.findById(countryId)
                            .orElseThrow(()-> new Hotelier(HttpStatus.BAD_REQUEST, "Invalid country")));
                    return state;
                }
                ).collect(Collectors.toList()))
                .stream().map(StateDto::fromState).collect(Collectors.toList());

    }
    @PostConstruct
    void addRoles(){
        Role guestRole = new Role();
        guestRole.setTitle(SystemRoles.GUEST);
        Role admin = new Role();
        admin.setTitle(SystemRoles.ADMIN);
        Role superAdmin = new Role();
        superAdmin.setTitle(SystemRoles.SUPER_ADMIN);
        List<Role> savedRoles = new ArrayList<>(List.of(guestRole, admin, superAdmin));
        List<Role> roles = roleRepo.findAll();
        if (roles.isEmpty())
            roleRepo.saveAll(savedRoles);
    }
}
