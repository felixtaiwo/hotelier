package com.hotelier.model.dto;

import com.hotelier.model.entity.Room;
import com.hotelier.service.FileService;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDto {
    Long id;
    @NotNull @NotBlank
    String roomNumber;
    @NotNull
    RoomCategoryDto category;
    public static RoomDto fromRoom(Room room, FileService fileService) {
        RoomDto roomDto = new RoomDto();
        roomDto.setId(room.getId());
        roomDto.setRoomNumber(room.getNumber());
        roomDto.setCategory(RoomCategoryDto.fromRoomCategory(room.getCategory(), fileService));
        return roomDto;
    }
}
