package com.hotelier.resource;


import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.FileDto;
import com.hotelier.model.dto.FileUploadDto;
import com.hotelier.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Constants;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;


@RestController @RequiredArgsConstructor
@RequestMapping("api/v1/file") @Transactional(rollbackFor = {Hotelier.class, NullPointerException.class, Exception.class, BindException.class, Constants.ConstantException.class})
@Tag(name="File", description = "file endpoints")
public class FileResource {
    Logger log = Logger.getLogger(this.getClass().getName());
    private final FileService fileService;
    @PostMapping(value = "upload", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@ModelAttribute FileUploadDto fileUploadDto) throws IOException {
        fileService.uploadFile(fileUploadDto);
    }
    @GetMapping("download/{fileId}")
    public FileDto uploadFile(@PathVariable Long fileId) throws IOException {
        return fileService.downloadFile(fileId);
    }

}
