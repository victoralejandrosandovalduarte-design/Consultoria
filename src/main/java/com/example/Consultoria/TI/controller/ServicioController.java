package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.*;
import com.example.Consultoria.TI.repository.ClienteRepository;
import com.example.Consultoria.TI.repository.TecnicoRepository;
import com.example.Consultoria.TI.repository.TipoServicioRepository;
import com.example.Consultoria.TI.repository.MaterialRepository;
import com.example.Consultoria.TI.service.*;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class ServicioController {
    
    private final ServicioService servicioService;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final MaterialRepository materialRepository;
    
    @GetMapping
public String listar(@RequestParam(required = false) String estado, Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/usuarios/login";

    List<Servicio> servicios;
    if ("ADMIN".equals(usuario.getRol())) {
        servicios = servicioService.findAll();
    } else if ("SOPORTE".equals(usuario.getRol())) {
        Tecnico tecnico = (Tecnico) session.getAttribute("tecnico");
        if (tecnico == null) {
            tecnico = tecnicoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
            session.setAttribute("tecnico", tecnico);
        }
        servicios = servicioService.obtenerServiciosPorTecnico(tecnico.getIdTecnico());
    } else { // CLIENTE
        servicios = servicioService.obtenerServiciosPorCliente(usuario.getCliente().getIdCliente());
    }

    if (estado != null) {
        servicios = servicios.stream().filter(s -> estado.equals(s.getEstado())).toList();
    }
    model.addAttribute("servicios", servicios);
    model.addAttribute("usuario", usuario);
    return "servicios/lista";
}
    
    @GetMapping("/nuevo")
    public String nuevoForm(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";
        
        Servicio servicio = Servicio.builder()
            .estado("PENDIENTE")
            .fechaHoraAgendamiento(LocalDateTime.now())
            .build();
        
        if ("CLIENTE".equals(usuario.getRol())) {
            servicio.setCliente(usuario.getCliente());
        }
        
        model.addAttribute("servicio", servicio);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("tipos", tipoServicioRepository.findAll());
        model.addAttribute("esAdminOSoporte", "ADMIN".equals(usuario.getRol()) || "SOPORTE".equals(usuario.getRol()));
        
        return "servicios/formulario";
    }
    @PostMapping("/{id}/detalle/{detalleId}/eliminar")
public String eliminarDetalle(@PathVariable Long id,
                              @PathVariable Long detalleId,
                              HttpSession session,
                              RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || (!"ADMIN".equals(usuario.getRol()) && !"SOPORTE".equals(usuario.getRol()))) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios/" + id;
    }
    try {
        servicioService.eliminarDetalle(detalleId);
        redirect.addFlashAttribute("exito", "Material eliminado");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
    }
    return "redirect:/servicios/" + id;
}
    @PostMapping
public String guardar(@ModelAttribute Servicio servicio,
                      HttpSession session,
                      RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/usuarios/login";

    try {
        // --- Validación y asignación del cliente ---
        if ("CLIENTE".equals(usuario.getRol())) {
            if (usuario.getCliente() == null) {
                throw new RuntimeException("El usuario no tiene un cliente asociado. Complete su perfil.");
            }
            servicio.setCliente(usuario.getCliente());
        } else {
            // Admin o Soporte: el cliente debe venir del formulario
            if (servicio.getCliente() == null || servicio.getCliente().getIdCliente() == null) {
                throw new RuntimeException("Debe seleccionar un cliente.");
            }
            Cliente cliente = clienteRepository.findById(servicio.getCliente().getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            servicio.setCliente(cliente);
        }

        // --- Validación del tipo de servicio ---
        if (servicio.getTipoServicio() == null || servicio.getTipoServicio().getIdTipoServicio() == null) {
            throw new RuntimeException("Debe seleccionar un tipo de servicio.");
        }
        if (servicio.getTecnicoAsignado() != null && servicio.getTecnicoAsignado().getIdTecnico() != null) {
    Tecnico tecnico = tecnicoRepository.findById(servicio.getTecnicoAsignado().getIdTecnico())
            .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
    servicio.setTecnicoAsignado(tecnico);
} else {
    servicio.setTecnicoAsignado(null);
}
        TipoServicio tipo = tipoServicioRepository.findById(servicio.getTipoServicio().getIdTipoServicio())
                .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado"));
        servicio.setTipoServicio(tipo);

        // (Opcional) Si el estado no viene, asignar por defecto
        if (servicio.getEstado() == null) {
            servicio.setEstado("PENDIENTE");
        }

        // Log para depuración
        System.out.println("Guardando servicio con cliente ID: " + servicio.getCliente().getIdCliente());
        System.out.println("Tipo servicio ID: " + servicio.getTipoServicio().getIdTipoServicio());

        servicioService.save(servicio);
        redirect.addFlashAttribute("exito", "Servicio guardado exitosamente");
        return "redirect:/servicios";

    } catch (Exception e) {
        e.printStackTrace();
        redirect.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        return "redirect:/servicios/nuevo";
    }
}
@PostMapping("/{id}/generar-presupuesto")
public String generarPresupuesto(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || (!"ADMIN".equals(usuario.getRol()) && !"SOPORTE".equals(usuario.getRol()))) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios/" + id;
    }
    try {
        servicioService.generarPresupuesto(id);
        redirect.addFlashAttribute("exito", "Presupuesto generado, ahora el cliente debe aprobarlo.");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/servicios/" + id;
}

@PostMapping("/{id}/asignar-tecnico")
public String asignarTecnico(@PathVariable Long id,
                             @RequestParam Long tecnicoId,
                             HttpSession session,
                             RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || (!"ADMIN".equals(usuario.getRol()) && !"SOPORTE".equals(usuario.getRol()))) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios/" + id;
    }
    try {
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
        servicioService.asignarTecnico(id, tecnico);
        redirect.addFlashAttribute("exito", "Técnico asignado, servicio en progreso.");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/servicios/" + id;
}
@PostMapping("/{id}/cancelar")
public String cancelar(@PathVariable Long id,
                       @RequestParam String comentario,
                       HttpSession session,
                       RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"CLIENTE".equals(usuario.getRol())) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios";
    }
    Servicio servicio = servicioService.findById(id);
    if (!servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios";
    }
    // Solo puede cancelar si está en PENDIENTE
    if (!"PENDIENTE".equals(servicio.getEstado())) {
        redirect.addFlashAttribute("error", "Solo se pueden cancelar servicios en estado PENDIENTE");
        return "redirect:/servicios/" + id;
    }
    servicioService.cancelarServicio(id, comentario, usuario);
    redirect.addFlashAttribute("exito", "Servicio cancelado");
    return "redirect:/servicios/" + id;
}
@PostMapping("/{id}/eliminar")
public String eliminarServicio(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios";
    }
    try {
        servicioService.eliminar(id);
        redirect.addFlashAttribute("exito", "Servicio eliminado correctamente");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
    }
    return "redirect:/servicios";
}
    
    @GetMapping("/{id}")
public String ver(@PathVariable Long id, Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/usuarios/login";

    // Cargar servicio con todas las relaciones
    Servicio servicio = servicioService.findByIdWithDetails(id);

    // Calcular totales
    double totalMateriales = servicio.getDetalles().stream()
            .mapToDouble(DetalleServicio::getSubtotal).sum();
    double costoManoObra = servicio.getCostoManoObra() != null ? servicio.getCostoManoObra() : 0.0;
    double presupuestoTotal = totalMateriales + costoManoObra;

    // Verificar acceso (según rol)
    boolean accesoPermitido = false;
    if ("ADMIN".equals(usuario.getRol())) {
        accesoPermitido = true;
    } else if ("SOPORTE".equals(usuario.getRol())) {
        Tecnico tecnico = (Tecnico) session.getAttribute("tecnico");
        if (tecnico != null && servicio.getTecnicoAsignado() != null && 
            servicio.getTecnicoAsignado().getIdTecnico().equals(tecnico.getIdTecnico())) {
            accesoPermitido = true;
        }
    } else if ("CLIENTE".equals(usuario.getRol())) {
        if (servicio.getCliente() != null && 
            servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
            accesoPermitido = true;
        }
    }

    if (!accesoPermitido) {
        return "redirect:/servicios";
    }

    // Agregar atributos al modelo (sin duplicados)
    model.addAttribute("servicio", servicio);
    model.addAttribute("usuario", usuario);
    model.addAttribute("totalMateriales", totalMateriales);
    model.addAttribute("costoManoObra", costoManoObra);
    model.addAttribute("presupuestoTotal", presupuestoTotal);
    model.addAttribute("mostrarPresupuesto", !"SOPORTE".equals(usuario.getRol()));
    model.addAttribute("materiales", materialRepository.findAll());
    model.addAttribute("tecnicos", tecnicoRepository.findAll());

    return "servicios/ver";
}
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/servicios";
        }
        
        Servicio servicio = servicioService.findById(id);
        model.addAttribute("servicio", servicio);
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("tipos", tipoServicioRepository.findAll());
        return "servicios/formulario";
    }
    @PostMapping("/{id}/detalle")
public String agregarDetalle(@PathVariable Long id,
                             @RequestParam Long materialId,
                             @RequestParam Integer cantidad,
                             HttpSession session,
                             RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || (!"ADMIN".equals(usuario.getRol()) && !"SOPORTE".equals(usuario.getRol()))) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios/" + id;
    }
    try {
        servicioService.agregarDetalle(id, materialId, cantidad);
        redirect.addFlashAttribute("exito", "Material agregado");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", "Error: " + e.getMessage());
    }
    return "redirect:/servicios/" + id;
}
    @PostMapping("/{id}/estado")
public String cambiarEstado(@PathVariable Long id,
                           @RequestParam String estado,
                           @RequestParam(required = false) String comentario,
                           HttpSession session,
                           RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios";
    }

    Servicio servicio = servicioService.findById(id);
    String estadoActual = servicio.getEstado();

    // Validar permisos según rol
    boolean permitido = false;
    if ("ADMIN".equals(usuario.getRol())) {
        permitido = true; // admin puede cambiar a cualquier estado
    } else if ("SOPORTE".equals(usuario.getRol())) {
        // Soporte solo puede cambiar de PENDIENTE a EN_PROGRESO o de EN_PROGRESO a COMPLETADO
        permitido = ("PENDIENTE".equals(estadoActual) && "EN_PROGRESO".equals(estado)) ||
                    ("EN_PROGRESO".equals(estadoActual) && "COMPLETADO".equals(estado));
    }

    if (!permitido) {
        redirect.addFlashAttribute("error", "No tiene permiso para cambiar a ese estado");
        return "redirect:/servicios/" + id;
    }

    servicioService.cambiarEstado(id, estado, comentario, usuario);
    redirect.addFlashAttribute("exito", "Estado actualizado");
    return "redirect:/servicios/" + id;
}
    @PostMapping("/{id}/mano-obra")
public String actualizarManoObra(@PathVariable Long id,
                                 @RequestParam Double costoManoObra,
                                 HttpSession session,
                                 RedirectAttributes redirect) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || (!"ADMIN".equals(usuario.getRol()) && !"SOPORTE".equals(usuario.getRol()))) {
        redirect.addFlashAttribute("error", "No autorizado");
        return "redirect:/servicios/" + id;
    }
    try {
        servicioService.actualizarManoObra(id, costoManoObra);
        redirect.addFlashAttribute("exito", "Costo de mano de obra actualizado");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/servicios/" + id;
}
    
    @PostMapping("/{id}/comentario")
    public String agregarComentario(@PathVariable Long id,
                                    @RequestParam String texto,
                                    HttpSession session,
                                    RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"SOPORTE".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol()))) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        servicioService.agregarComentario(id, texto, usuario);
        redirect.addFlashAttribute("exito", "Comentario agregado");
        return "redirect:/servicios/" + id;
    }
    @PostMapping("/{id}/asignar")
    public String asignarPresupuesto(@PathVariable Long id,
                                     @RequestParam Double presupuesto,
                                     @RequestParam String moneda,
                                     @RequestParam Long tecnicoId,
                                     RedirectAttributes redirect,
                                     HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
            
        }
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
            .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
        
        servicioService.asignarPresupuesto(id, presupuesto, moneda, tecnico);
        redirect.addFlashAttribute("exito", "Presupuesto asignado");
        return "redirect:/servicios/" + id;
    }
    @PostMapping("/{id}/aceptar")
    public String aceptar(@PathVariable Long id, RedirectAttributes redirect, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"CLIENTE".equals(usuario.getRol())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        
        Servicio servicio = servicioService.findById(id);
        if (!servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        
        servicioService.aceptarServicio(id);
        redirect.addFlashAttribute("exito", "Servicio aceptado");
        return "redirect:/servicios/" + id;
    }
    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @RequestParam String comentario,
                           RedirectAttributes redirect,
                           HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"CLIENTE".equals(usuario.getRol())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        
        Servicio servicio = servicioService.findById(id);
        if (!servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        servicioService.rechazarServicio(id, comentario, usuario);
        redirect.addFlashAttribute("exito", "Servicio rechazado");
        return "redirect:/servicios/" + id;
    }
    //GENERAR PRESUPUESTO:
    @GetMapping("/{id}/pdf")
public void generarPdf(@PathVariable Long id, 
                       HttpServletResponse response, 
                       HttpSession session) throws IOException {
    
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    Servicio servicio = servicioService.findByIdWithDetails(id);
    
    // Verificar acceso: solo el cliente del servicio o admin pueden ver el PDF
    boolean accesoPermitido = false;
    if ("ADMIN".equals(usuario.getRol())) {
        accesoPermitido = true;
    } else if ("CLIENTE".equals(usuario.getRol())) {
        if (servicio.getCliente() != null && 
            servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
            accesoPermitido = true;
        }
    }
    
    if (!accesoPermitido) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }

    // Configurar respuesta HTTP
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=presupuesto_" + servicio.getNumeroOrden() + ".pdf");

    // Calcular totales
    double totalMateriales = servicio.getDetalles().stream()
            .mapToDouble(DetalleServicio::getSubtotal).sum();
    double costoManoObra = servicio.getCostoManoObra() != null ? servicio.getCostoManoObra() : 0.0;
    double total = totalMateriales + costoManoObra;

    // Crear documento PDF con iText
    Document document = new Document();
    try {
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Presupuesto de Servicio", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));

        // Información general
        document.add(new Paragraph("Número de orden: " + servicio.getNumeroOrden()));
        document.add(new Paragraph("Cliente: " + servicio.getCliente().getNombre() + " " + servicio.getCliente().getApellido()));
        document.add(new Paragraph("Empresa: " + (servicio.getCliente().getEmpresa() != null ? servicio.getCliente().getEmpresa() : "N/A")));
        document.add(new Paragraph("Tipo de servicio: " + servicio.getTipoServicio().getNombre()));
        document.add(new Paragraph("Descripción: " + (servicio.getReclamo() != null ? servicio.getReclamo() : "Sin descripción")));
        document.add(new Paragraph("Fecha de creación: " + (servicio.getFechaCreacion() != null ? servicio.getFechaCreacion().toString() : "N/A")));
        document.add(new Paragraph(" "));

        // Tabla de materiales
        document.add(new Paragraph("Detalle de materiales:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell("Material");
        table.addCell("Cantidad");
        table.addCell("Precio Unit. (Gs.)");
        table.addCell("Subtotal (Gs.)");

        if (servicio.getDetalles().isEmpty()) {
            table.addCell("No hay materiales");
            table.addCell("-");
            table.addCell("-");
            table.addCell("-");
        } else {
            for (DetalleServicio detalle : servicio.getDetalles()) {
                table.addCell(detalle.getMaterial().getNombre());
                table.addCell(String.valueOf(detalle.getCantidad()));
                table.addCell(String.format("%,.0f", detalle.getMaterial().getPrecio()));
                table.addCell(String.format("%,.0f", detalle.getSubtotal()));
            }
        }
        document.add(table);
        document.add(new Paragraph(" "));

        // Resumen de costos
        document.add(new Paragraph("Total materiales: " + String.format("%,.0f", totalMateriales) + " Gs."));
        document.add(new Paragraph("Costo mano de obra: " + String.format("%,.0f", costoManoObra) + " Gs."));
        document.add(new Paragraph("TOTAL PRESUPUESTO: " + String.format("%,.0f", total) + " Gs.", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));

        document.close();
    } catch (DocumentException e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
    
    
    
    }