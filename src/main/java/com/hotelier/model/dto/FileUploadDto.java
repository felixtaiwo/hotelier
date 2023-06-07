package com.hotelier.model.dto;

import com.hotelier.model.enums.EntityType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
@NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadDto {
    @NotNull(message = "File cannot be null")
    MultipartFile file;
    @NotNull(message = "Entity cannot be null")
    EntityType entityType;
    @NotNull(message = "Entity id be null")
    @Positive @Min(1)
    Long entityId;
}
