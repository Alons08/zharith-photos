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
    public String handleMaxSizeException(Exception ex, Model model) {
        model.addAttribute("error", "El archivo es demasiado grande. El máximo permitido es 10MB por foto.");
        return "subir-foto";
    }
}
