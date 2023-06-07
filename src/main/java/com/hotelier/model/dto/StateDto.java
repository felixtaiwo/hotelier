package com.hotelier.model.dto;

import com.hotelier.model.entity.Country;
import com.hotelier.model.entity.State;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StateDto {
    Long id;
    String title;
    CountryDto countryDto;
    @Setter @Getter @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CountryDto{
        Long id;
        String title;
        String currency;
        String phoneCode;
        StateDto[] states;
        public static CountryDto fromCountry(Country country, boolean state){
            CountryDto countryDto = new CountryDto();
            countryDto.setId(country.getId());
            countryDto.setTitle(country.getName());
            countryDto.setPhoneCode(country.getPhoneCode());
            countryDto.setCurrency(country.getCurrency());
            countryDto.setStates(state?getState(country.getStates()):null);
            return countryDto;
        }

        public static Country toCountry(CountryDto countryDto){
            Country country = new Country();
            country.setCurrency(countryDto.getCurrency());
            country.setPhoneCode(countryDto.getPhoneCode());
            country.setName(countryDto.getTitle());
            country.setId(countryDto.getId());
            return country;
        }

        private static StateDto[] getState(List<State> states) {
            if(states == null){
                return null;
            }
            List<StateDto> collect = states.stream().map(StateDto::fromState).collect(Collectors.toList());
            StateDto[] result = new StateDto[collect.size()];
            collect.toArray(result);
            return result;
        }

    }
    public static StateDto fromState(State state){
        StateDto stateDto = new StateDto();
        stateDto.setId(state.getId());
        stateDto.setTitle(state.getName());
        stateDto.setCountryDto(CountryDto.fromCountry(state.getCountry(), false));
        return stateDto;
    }
    public static State toState(StateDto stateDto){
        if(stateDto == null)
            return null;
        State state = new State();
        state.setId(stateDto.getId());
        state.setName(stateDto.getTitle());
        return state;
    }
}
