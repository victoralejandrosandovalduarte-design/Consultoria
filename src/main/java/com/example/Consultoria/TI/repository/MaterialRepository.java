package com.example.Consultoria.TI.repository;

import com.example.Consultoria.TI.modelo.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {}