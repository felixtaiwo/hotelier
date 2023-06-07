package com.hotelier.resource;


import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.StateDto;
import com.hotelier.service.Metaservice;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Constants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.BindException;
import java.util.List;
import java.util.logging.Logger;


@RestController @RequiredArgsConstructor
@RequestMapping("api/v1/metadata") @Transactional(rollbackFor = {Hotelier.class, NullPointerException.class, Exception.class, BindException.class, Constants.ConstantException.class})
@Tag(name="Metadata", description = "metadata endpoints")
public class MetaDataResource {
    Logger log = Logger.getLogger(this.getClass().getName());
    private final Metaservice metaservice;
    @GetMapping("country")
    public List<StateDto.CountryDto> getSupportedCountries(){
        return metaservice.getSupportedCountry();
    }

    @PostMapping("country")
    public List<StateDto.CountryDto> createCountry(@Valid @RequestBody StateDto.CountryDto[] countryDtos){
        return metaservice.createCountry(countryDtos);
    }
    @PostMapping("state/{countryId}")
    public List<StateDto> createCountry(@Valid @RequestBody StateDto[] stateDtos, Long countryId){
        return metaservice.createState(stateDtos, countryId);
    }

}
