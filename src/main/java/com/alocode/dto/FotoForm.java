package com.alocode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class FotoForm {
    @NotBlank(message = "Por favor ingresa tu nombre")
    private String nombreInvitado;
    
    @NotNull(message = "Debes seleccionar al menos una foto")
    @Size(min = 1, message = "Debes seleccionar al menos una foto")
    private MultipartFile[] archivos;
}