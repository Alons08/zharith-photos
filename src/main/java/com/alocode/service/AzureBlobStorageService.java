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
        // Validar tamaño de archivo
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IOException("El archivo " + file.getOriginalFilename() + " excede el límite de 10MB");
        }
        
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        
        try {
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
        } catch (Exception e) {
            log.error("Error al subir archivo a Azure Blob Storage: {}", e.getMessage());
            throw new IOException("Error al subir el archivo: " + e.getMessage());
        }
    }
}