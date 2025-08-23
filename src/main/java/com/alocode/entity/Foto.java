package com.alocode.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Foto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreArchivo;
    private String url;
    private String nombreInvitado;
    private LocalDateTime fechaSubida;
    
    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }
}