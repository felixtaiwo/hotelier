package com.hotelier.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter @Getter @NotNull @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryDto {
    Integer count;
    Integer status;
    @Setter @Getter @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FromSummaryDto{
        ReservationDto.ReservationStatus status;
        Integer count;
    }
    public static FromSummaryDto reform(SummaryDto summaryDto){
        FromSummaryDto fromSummaryDto = new FromSummaryDto();
        fromSummaryDto.setCount(summaryDto.getCount());
        fromSummaryDto.setStatus(ReservationDto.ReservationStatus.getStatus(summaryDto.getStatus()));
        return fromSummaryDto;
    }

}
