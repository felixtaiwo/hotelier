package com.hotelier.service;


import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.FileDto;
import com.hotelier.model.dto.FileUploadDto;
import com.hotelier.model.entity.FileStore;
import com.hotelier.model.enums.EntityType;
import com.hotelier.model.repository.FileStoreRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStoreRepo fileStoreRepo;
    private final S3Client s3;
    private final Tika tika = new Tika();
    private final ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = Logger.getLogger(this.getClass().getName());


    public void uploadFile(FileUploadDto file) throws IOException {
        boolean inMemory = false;
        HashMap<String, String> userMetaData = extractFileMetaData(file.getFile());
        FileStore fileStore = new FileStore();
        fileStore.setEntityId(file.getEntityId());
        fileStore.setEntityType(file.getEntityType().name());
        fileStore.setMetaData(objectMapper.writeValueAsString(userMetaData));
        fileStore.setFileStorage(inMemory? FileStore.FileStorage.LOCAL_SERVER: FileStore.FileStorage.AMAZON_S3);
        if (inMemory) {
            Path pathToStore = Paths.get("hotelier/" + getKey(file.getEntityType(), file.getEntityId(), userMetaData.get("fileName")));
            Path saveHere;
            try {
                if (!Files.exists(pathToStore)) {
                    Files.createDirectories(pathToStore);
                }
                saveHere = Paths.get(pathToStore.resolve(Objects.requireNonNull((file.getFile().getOriginalFilename()))).toUri());
                Files.copy(file.getFile().getInputStream(), saveHere);
            } catch (Exception e) {
                throw new Hotelier(HttpStatus.BAD_REQUEST, "Could not store the file. Error: " + e.getMessage());
            }
            fileStore.setUrl(saveHere.toString());
        }
        else {
            PutObjectRequest request = PutObjectRequest.builder().bucket(AwsS3ClientConfig.bucketName).key(getKey(file.getEntityType(), file.getEntityId(), userMetaData.get("fileName"))).metadata(userMetaData).build();
            s3.putObject(request, RequestBody.fromInputStream(file.getFile().getInputStream(),file.getFile().getInputStream().available()));
            GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(AwsS3ClientConfig.bucketName).key(getKey(file.getEntityType(), file.getEntityId(), userMetaData.get("fileName"))).build();
            URL url = s3.utilities().getUrl(getUrlRequest);
            fileStore.setUrl(url.toString());
        }
        fileStoreRepo.save(fileStore);

    }

    private String getKey(EntityType entityType, Long entityId, String id) {
        String key =  "test/" + entityType + "/" + entityId + "/" + id;
        logger.info("key ::: " + key);
        return key;
    }

    private HashMap<String, String> extractFileMetaData(MultipartFile file) throws IOException {
        List<String> validFiles = List.of(new String[]{"image/jpeg", "image/jpg", "image/png", "application/pdf"});
        String extension = tika.detect(file.getBytes());
        if(!validFiles.contains(extension)){
            throw new Hotelier(HttpStatus.BAD_REQUEST, "Invalid File format. \n Valid file format: "+validFiles);
        }
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("fileSize", String.valueOf(file.getSize()));
        metadata.put("fileName",   UUID.randomUUID().toString());
        metadata.put("extension", extension);
        logger.info("file metadata extracted");
        return metadata;
    }

    public FileDto downloadFile(Long fileId) throws IOException {
        FileStore file = fileStoreRepo.findById(fileId)
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "File does not exist"));
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};
        FileDto fileDto = new FileDto();
        fileDto.setId(fileId);
        fileDto.setMetadata(objectMapper.readValue(file.getMetaData(), typeRef));
        fileDto.setUrl(file.getUrl());
        return fileDto;
    }

    public FileDto[] downloadFile(List<FileStore> files) throws IOException {
        if(files == null){
            return null;
        }
        List<FileDto> fileDtos = new ArrayList<>();
        for(FileStore fileStore: files){
            fileDtos.add(this.downloadFile(fileStore.getId()));
        }
        return fileDtos.toArray(new FileDto[0]);
    }

}
