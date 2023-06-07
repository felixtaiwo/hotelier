package com.hotelier.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {
    Long id;
    String base64;
    String url;
    HashMap<String, Object> metadata;


}
