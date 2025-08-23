package com.alocode.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    public String handleMaxSizeException(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "El archivo es demasiado grande. El máximo permitido es 10MB por foto y 80MB en total.");
        return "redirect:/subir";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "La suma total de archivos supera el máximo permitido (80MB). Por favor selecciona menos o archivos más pequeños.");
        return "redirect:/subir";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado. Por favor intenta nuevamente.");
        return "redirect:/subir";
    }
}