package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.repository.ClienteRepository;
import com.example.Consultoria.TI.repository.ServicioRepository;
import com.example.Consultoria.TI.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository; // se puede mantener por si acaso
    private final BCryptPasswordEncoder passwordEncoder;
    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        // Validar email único
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Asegurar estado activo
        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }

        // El cliente se guardará automáticamente gracias a cascade = CascadeType.ALL
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

    public Optional<Usuario> autenticar(String email, String clave) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println("Clave almacenada: " + usuario.getClave());
            if (passwordEncoder.matches(clave, usuario.getClave())) {
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

    @Transactional
    public void cambiarClave(Long id, String nuevaClave) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setClave(passwordEncoder.encode(nuevaClave));
        usuarioRepository.save(usuario);
    }
}