package com.alocode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alocode.entity.Foto;

import java.util.List;

public interface FotoRepository extends JpaRepository<Foto, Long> {
    List<Foto> findAllByOrderByFechaSubidaDesc();
}