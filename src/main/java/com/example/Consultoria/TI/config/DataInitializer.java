package com.example.Consultoria.TI.config;

import com.example.Consultoria.TI.modelo.*;
import com.example.Consultoria.TI.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final MaterialRepository materialRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Configurable via env vars: ADMIN_EMAIL / ADMIN_PASSWORD
    // Fallback defaults are safe-ish but you should always override them in
    // production
    @Value("${admin.email:admin@consultoria.com}")
    private String adminEmail;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    // NUEVOS: Credenciales Soporte
    @Value("${soporte.email:soporte@consultoria.com}")
    private String soporteEmail;

    @Value("${soporte.password:soporte123}")
    private String soportePassword;

    // NUEVOS: Credenciales Cliente Prueba
    @Value("${cliente.email:cliente@ejemplo.com}")
    private String clienteEmail;

    @Value("${cliente.password:cliente123}")
    private String clientePassword;

    @PostConstruct
    @Transactional
    public void init() {
        // Seed catalog data unconditionally (idempotent: skip if already present)
        seedTiposServicio();
        seedMateriales();

        // Create initial users only on first run (no users in DB yet)
        if (usuarioRepository.count() == 0) {
            crearUsuariosIniciales();
        }
    }

    private void crearUsuariosIniciales() {
        System.out.println("🚀 Primer inicio — Generando cuentas del sistema...");

        // 1. Crear Administrador
        Usuario admin = new Usuario();
        admin.setEmail(adminEmail);
        admin.setClave(passwordEncoder.encode(adminPassword));
        admin.setRol("ADMIN");
        admin.setEstado(true);
        usuarioRepository.save(admin);

        // 2. Crear Personal de Soporte
        Usuario soporte = new Usuario();
        soporte.setEmail(soporteEmail);
        soporte.setClave(passwordEncoder.encode(soportePassword));
        soporte.setRol("SOPORTE");
        soporte.setEstado(true);
        usuarioRepository.save(soporte);

        // 3. Crear Cliente de Prueba
        Usuario cliente = new Usuario();
        cliente.setEmail(clienteEmail);
        cliente.setClave(passwordEncoder.encode(clientePassword));
        cliente.setRol("CLIENTE");
        cliente.setEstado(true);
        usuarioRepository.save(cliente);

        System.out.println("✅ Usuarios base creados correctamente (Admin, Soporte y Cliente).");
    }

    private void seedTiposServicio() {
        if (tipoServicioRepository.count() > 0)
            return;

        String[][] tipos = {
                { "Consultoría", "true" },
                { "Redes", "true" },
                { "Hardware", "false" },
                { "Software", "true" },
                { "Capacitación", "true" }
        };

        for (String[] tipo : tipos) {
            TipoServicio ts = new TipoServicio();
            ts.setNombre(tipo[0]);
            ts.setCertificada(Boolean.parseBoolean(tipo[1]));
            tipoServicioRepository.save(ts);
        }
        System.out.println("✅ Tipos de servicio creados.");
    }

    private void seedMateriales() {
        if (materialRepository.count() > 0)
            return;

        Object[][] materiales = {
                { "Router WiFi", 150000.0 },
                { "Switch 24 puertos", 300000.0 },
                { "Cable UTP Cat6", 25000.0 },
                { "Memoria RAM 8GB", 450000.0 },
                { "M.2 SSD 250GB", 800000.0 },
                { "Disco HDD 2 TB", 600000.0 },
                { "Disco SSD 1TB", 1800000.0 },
                { "Mano de Obra Especializada", 150000.0 },
                { "Hora Desarrollo", 190000.0 },
                { "Teclado", 150000.0 },
                { "Mouse", 120000.0 },
                { "DVR Hikvision", 6000000.0 },
                { "Hikvision - Hilook", 1800000.0 },
                { "Relevamiento Técnico", 120000.0 }
        };

        for (Object[] m : materiales) {
            Material material = new Material();
            material.setNombre((String) m[0]);
            material.setPrecio((Double) m[1]);
            materialRepository.save(material);
        }
        System.out.println("✅ Materiales creados.");
    }
}
