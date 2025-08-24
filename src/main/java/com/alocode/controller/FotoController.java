package com.alocode.controller;

import com.alocode.dto.FotoForm;
import com.alocode.entity.Foto;
import com.alocode.repository.FotoRepository;
import com.alocode.service.AzureBlobStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FotoController {

    private final FotoRepository fotoRepository;
    private final AzureBlobStorageService blobStorageService;

    @GetMapping("/")
    public String galeria(Model model) {
    // Cargar solo la primera página
    Pageable pageable = PageRequest.of(0, 12, Sort.by("fechaSubida").descending());
    Page<Foto> fotoPage = fotoRepository.findAll(pageable);

    long totalFotos = fotoRepository.count();
    model.addAttribute("fotos", fotoPage.getContent());
    model.addAttribute("totalPages", fotoPage.getTotalPages());
    model.addAttribute("currentPage", 0);
    model.addAttribute("hasNext", fotoPage.hasNext());
    model.addAttribute("totalFotos", totalFotos);

    return "galeria";
    }

    // Endpoint para cargar más fotos (scroll infinito)
    @GetMapping("/cargar-mas-fotos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cargarMasFotos(@RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("fechaSubida").descending());
        Page<Foto> fotoPage = fotoRepository.findAll(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("fotos", fotoPage.getContent());
        response.put("hasNext", fotoPage.hasNext());
        response.put("nextPage", page + 1);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subir")
    public String mostrarFormulario(Model model) {
        if (!model.containsAttribute("fotoForm")) {
            model.addAttribute("fotoForm", new FotoForm());
        }
        return "subir-foto";
    }

    @PostMapping("/subir")
    public String subirFotos(
        @Valid @ModelAttribute("fotoForm") FotoForm fotoForm,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        // Validar tamaño total de archivos
        long totalSize = 0;
        if (fotoForm.getArchivos() != null) {
            for (MultipartFile archivo : fotoForm.getArchivos()) {
                totalSize += archivo.getSize();
                if (archivo.getSize() > 10 * 1024 * 1024) {
                    result.rejectValue("archivos", "error.fotoForm", 
                            "El archivo " + archivo.getOriginalFilename() + " excede el límite de 10MB");
                }
            }
            
            if (totalSize > 80 * 1024 * 1024) {
                result.rejectValue("archivos", "error.fotoForm", 
                        "El tamaño total de archivos excede el límite de 80MB");
            }
        }

        if (result.hasErrors()) {
            return "subir-foto";
        }

        List<String> errores = new ArrayList<>();
        int exitosas = 0;

        try {
            for (MultipartFile archivo : fotoForm.getArchivos()) {
                try {
                    String url = blobStorageService.uploadFile(archivo);
                    
                    Foto foto = new Foto();
                    foto.setNombreArchivo(archivo.getOriginalFilename());
                    foto.setUrl(url);
                    foto.setNombreInvitado(fotoForm.getNombreInvitado());
                    
                    fotoRepository.save(foto);
                    exitosas++;
                } catch (IOException e) {
                    errores.add(archivo.getOriginalFilename() + ": " + e.getMessage());
                    log.error("Error al subir archivo: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error inesperado. Por favor intenta nuevamente.");
            return "subir-foto";
        }

        if (!errores.isEmpty()) {
            if (exitosas == 0) {
                model.addAttribute("error", "No se pudieron subir las fotos: " + String.join(", ", errores));
                return "subir-foto";
            } else {
                String plural = exitosas == 1 ? "foto subida" : "fotos subidas";
                redirectAttributes.addFlashAttribute("warning", 
                    "¡" + exitosas + " " + plural + ", pero hubo errores con: " + String.join(", ", errores));
            }
        } else {
            String plural = exitosas == 1 ? "foto subida" : "fotos subidas";
            redirectAttributes.addFlashAttribute("success", 
                "¡" + exitosas + " " + plural + " correctamente!");
        }

        return "redirect:/";
    }
}