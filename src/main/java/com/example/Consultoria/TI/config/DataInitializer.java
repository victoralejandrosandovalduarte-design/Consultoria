package com.example.Consultoria.TI.config;

import com.example.Consultoria.TI.modelo.*;
import com.example.Consultoria.TI.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final TecnicoRepository tecnicoRepository;
    private final MaterialRepository materialRepository;
    private final BCryptPasswordEncoder passwordEncoder; 

    @PostConstruct
    @Transactional
    public void init() {
        // Solo crear datos si no existen
        if (usuarioRepository.count() == 0) {
            crearDatosIniciales();
        }
    }

    private void crearDatosIniciales() {
        System.out.println("🚀 Creando datos iniciales...");
        try {
            // 1. Crear tipos de servicio primero
            crearTiposServicio();
            // 2. Crear materiales
            crearMateriales();
            // 3. Crear cliente
            Cliente cliente = crearCliente();            
            // 4. Crear usuario cliente
            crearUsuarioCliente(cliente);            
            // 5. Crear usuario admin
            crearUsuarioAdmin();            
            // 6. Crear usuario soporte y técnico
            crearUsuarioSoporteYTecnico();            
            System.out.println("✅ Datos iniciales creados exitosamente!");            
        } catch (Exception e) {
            System.err.println("❌ Error creando datos iniciales: " + e.getMessage());
            e.printStackTrace();
        }
    }    

    private void crearTiposServicio() {
        String[][] tipos = {
            {"Consultoría", "true"},
            {"Redes", "true"},
            {"Hardware", "false"},
            {"Software", "true"},
            {"Capacitación", "true"}
                
        };
        
        for (String[] tipo : tipos) {
            TipoServicio ts = new TipoServicio();
            ts.setNombre(tipo[0]);
            ts.setCertificada(Boolean.parseBoolean(tipo[1]));
            tipoServicioRepository.save(ts);
        }
    }

    private void crearMateriales() {
        Object[][] materiales = {
            {"Router WiFi", 150000.000},
            {"Switch 24 puertos", 300000.000},
            {"Cable UTP Cat6", 25000.00},
            {"Memoria RAM 8GB", 450000.000},
            {"M.2 SSD 250GB", 800000.000},
            {"Disco HDD 2 TB", 600000.000},
{"Disco SSD 1TB", 1800000.000},
{"Hora Desarrollo", 190000.000},
{"Teclado", 150000.0},
{"Mouse", 120000.0},
{"DVR Hikvision", 6000000.000},
{"Hikvision - Hilook", 1800000.000},
{"Relevamiento Técnico", 120000.000}
        }; 
        for (Object[] material : materiales) {
            Material m = new Material();
            m.setNombre((String) material[0]);
            m.setPrecio((Double) material[1]);
            materialRepository.save(m);
        }
    }

    private Cliente crearCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Victor");
        cliente.setApellido("Sandoval");
        cliente.setEmpresa("Empresa S.A.");
        cliente.setCiRuc("5592530");
        cliente.setCiudad("Mariano");
        cliente.setLugarMantenimiento("Central casa propia");
        return clienteRepository.save(cliente);
    }    

    private void crearUsuarioCliente(Cliente cliente) {        
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@ejemplo.com");
        usuario.setClave(passwordEncoder.encode("cliente123")); // Encriptado
        usuario.setRol("CLIENTE");
        usuario.setEstado(true);
        usuario.setCliente(cliente); // Relación ya establecida
        usuarioRepository.save(usuario);
    }    

    private void crearUsuarioAdmin() {
        Usuario admin = new Usuario();
        admin.setEmail("admin@consultoria.com");
        admin.setClave(passwordEncoder.encode("admin123")); // Encriptado
        admin.setRol("ADMIN");
        admin.setEstado(true);
        usuarioRepository.save(admin);
    }    
    
    private void crearUsuarioSoporteYTecnico() {
        // Crear usuario soporte
        Usuario soporte = new Usuario();
        soporte.setEmail("soporte@consultoria.com");
        soporte.setClave(passwordEncoder.encode("soporte123")); // Encriptado
        soporte.setRol("SOPORTE");
        soporte.setEstado(true);
        soporte = usuarioRepository.save(soporte);
        // Crear técnico asociado
        Tecnico tecnico = new Tecnico();
        tecnico.setNombre("El CHAPO");
        tecnico.setUsuario(soporte);
        tecnicoRepository.save(tecnico);
    }
}