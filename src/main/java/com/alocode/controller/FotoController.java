package com.alocode.controller;

import com.alocode.dto.FotoForm;
import com.alocode.entity.Foto;
import com.alocode.repository.FotoRepository;
import com.alocode.service.AzureBlobStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class FotoController {

    private final FotoRepository fotoRepository;
    private final AzureBlobStorageService blobStorageService;

    @GetMapping("/")
    public String galeria(Model model) {
        model.addAttribute("fotos", fotoRepository.findAllByOrderByFechaSubidaDesc());
        return "galeria";
    }

    @GetMapping("/subir")
    public String mostrarFormulario(Model model) {
        model.addAttribute("fotoForm", new FotoForm());
        return "subir-foto";
    }

    @PostMapping("/subir")
    public String subirFotos(
        @Valid @ModelAttribute("fotoForm") FotoForm fotoForm,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            return "subir-foto";
        }

        try {
            for (MultipartFile archivo : fotoForm.getArchivos()) {
                String url = blobStorageService.uploadFile(archivo);
                
                Foto foto = new Foto();
                foto.setNombreArchivo(archivo.getOriginalFilename());
                foto.setUrl(url);
                foto.setNombreInvitado(fotoForm.getNombreInvitado());
                
                fotoRepository.save(foto);
            }
        } catch (IOException e) {
            model.addAttribute("error", "Ocurrió un error al subir las fotos. Por favor intenta nuevamente.");
            return "subir-foto";
        }

        return "redirect:/";
    }
}