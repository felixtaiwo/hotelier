package com.hotelier.model.dto;

import com.hotelier.model.entity.RoomCategory;
import com.hotelier.service.FileService;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomCategoryDto {
    Long id;
    String title;
    Double price;
    PropertyDto property;
    FeatureDto[] features;
    FileDto[] files;
    Integer guestSize;

    @SneakyThrows
    public static RoomCategoryDto fromRoomCategory(RoomCategory category, FileService fileService){
        RoomCategoryDto roomCategoryDto = new RoomCategoryDto();
        roomCategoryDto.setPrice(category.getPrice());
        roomCategoryDto.setId(category.getId());
        roomCategoryDto.setTitle(category.getName());
        roomCategoryDto.setFeatures(category.getFeatures()== null?null:category.getFeatures().stream().map(FeatureDto::fromFeature).toArray(FeatureDto[]::new));
        roomCategoryDto.setGuestSize(category.getGuestSize());
        roomCategoryDto.setFiles(fileService==null? null: fileService.downloadFile(category.getFiles()));
        return roomCategoryDto;

    }

    public static RoomCategory toRoomCategory(RoomCategoryDto roomCategoryDto) {
        if (roomCategoryDto == null)
            return null;
        RoomCategory roomCategory = new RoomCategory();
        roomCategory.setId(roomCategoryDto.getId());
        return roomCategory;
    }
}
