package com.hotelier.model.dto;

import com.hotelier.model.entity.Feature;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureDto {
    private Long id;
    private String name;

    public static Feature toFeature(FeatureDto featureDto){
        Feature feature = new Feature();
        feature.setId(featureDto.getId());
        feature.setName(featureDto.getName());
        return feature;
    }
    public static FeatureDto fromFeature(Feature feature){
        FeatureDto featureDto = new FeatureDto();
        featureDto.setId(feature.getId());
        featureDto.setName(feature.getName());
        return featureDto;
    }
}
