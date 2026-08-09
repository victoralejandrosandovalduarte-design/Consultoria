package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.Cliente;
import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.repository.ClienteRepository;
import com.example.Consultoria.TI.repository.ServicioRepository;
import com.example.Consultoria.TI.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ServicioRepository servicioRepository;
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    
    @Transactional
public Usuario guardar(Usuario usuario) {
    // Validar email único
    if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
        throw new RuntimeException("El email ya está registrado");
    }

    // Si el usuario tiene un cliente nuevo (sin ID), guardarlo primero
    if (usuario.getCliente() != null && usuario.getCliente().getIdCliente() == null) {
        Cliente clienteGuardado = clienteRepository.save(usuario.getCliente());
        usuario.setCliente(clienteGuardado);
    }

    // Encriptar contraseña
    usuario.setClave(passwordEncoder.encode(usuario.getClave()));
    return usuarioRepository.save(usuario);
}
    @Transactional
public void eliminar(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Si es cliente, verificar que no tenga servicios
    if ("CLIENTE".equals(usuario.getRol()) && usuario.getCliente() != null) {
        long count = servicioRepository.countByClienteIdCliente(usuario.getCliente().getIdCliente());
        if (count > 0) {
            throw new RuntimeException(
                "No se puede eliminar el cliente porque tiene " + count + " servicio(s) asociado(s)."
            );
        }
    }

    usuarioRepository.delete(usuario);
}
    
    public boolean verificarClave(String clavePlana, String hashAlmacenado) {
        return passwordEncoder.matches(clavePlana, hashAlmacenado);
    }

    public Optional<Usuario> autenticar(String email, String clave) {
        log.info(">>> autenticar: buscando email='{}'", email);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        log.info(">>> autenticar: findByEmail presente={}", usuarioOpt.isPresent());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            log.info(">>> autenticar: hash almacenado='{}'", usuario.getClave());
            boolean matches = passwordEncoder.matches(clave, usuario.getClave());
            log.info(">>> autenticar: BCrypt matches={}", matches);
            if (matches) {
                return usuarioOpt;
            }
        }
        return Optional.empty();
    }
    
    @Transactional
public void actualizarEmail(Long id, String email) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    usuario.setEmail(email);
}
    
    // Método para cambiar clave (futuro uso)
    @Transactional
    public void cambiarClave(Long id, String nuevaClave) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setClave(passwordEncoder.encode(nuevaClave));
        usuarioRepository.save(usuario);
    }
}