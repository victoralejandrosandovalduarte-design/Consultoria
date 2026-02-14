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
            {"Router WiFi", 150.00},
            {"Switch 24 puertos", 300.00},
            {"Cable UTP Cat6", 2.50},
            {"Memoria RAM 8GB", 45.00},
            {"Disco SSD 500GB", 60.00}
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
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setEmpresa("Empresa Ejemplo S.A.");
        cliente.setCiRuc("1234567890");
        cliente.setCiudad("Quito");
        cliente.setLugarMantenimiento("Oficina Central");
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
        tecnico.setNombre("Carlos Rodríguez");
        tecnico.setUsuario(soporte);
        tecnicoRepository.save(tecnico);
    }
}