package com.alocode.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobHttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class AzureBlobStorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        
        BlobContainerClient containerClient = new BlobContainerClientBuilder()
            .connectionString(connectionString)
            .containerName(containerName)
            .buildClient();

        if (!containerClient.exists()) {
            containerClient.create();
            log.info("Contenedor '{}' creado exitosamente", containerName);
        }

        BlobClient blobClient = containerClient.getBlobClient(fileName);
        
        BlobHttpHeaders headers = new BlobHttpHeaders()
            .setContentType(file.getContentType());
        
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        blobClient.setHttpHeaders(headers);
        
        log.info("Archivo '{}' subido correctamente", fileName);
        return blobClient.getBlobUrl();
    }
}