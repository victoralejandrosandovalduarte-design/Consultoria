package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Servicio;
import com.example.Consultoria.TI.modelo.Tecnico;
import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.service.ServicioService;
import com.example.Consultoria.TI.repository.TecnicoRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ServicioService servicioService;
    private final TecnicoRepository tecnicoRepository;

    @GetMapping
    public String verReportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long tecnicoId,
            HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/principal";
        }

        // Convertir fechas a LocalDateTime (inicio del día a fin del día)
        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : null;

        // Obtener servicios filtrados
System.out.println("Fecha inicio recibida: " + fechaInicio);
System.out.println("Fecha fin recibida: " + fechaFin);
System.out.println("inicio convertido: " + inicio);
System.out.println("fin convertido: " + fin);
List<Servicio> servicios = servicioService.findByFilters(inicio, fin, estado, tecnicoId);
System.out.println("Cantidad de servicios encontrados: " + servicios.size());
        // Estadísticas
        Map<String, Long> serviciosPorEstado = servicios.stream()
                .collect(Collectors.groupingBy(Servicio::getEstado, Collectors.counting()));

        Map<String, Long> serviciosPorTecnico = servicios.stream()
                .filter(s -> s.getTecnicoAsignado() != null)
                .collect(Collectors.groupingBy(s -> s.getTecnicoAsignado().getNombre(), Collectors.counting()));

        double totalPresupuestosGenerados = servicios.stream()
                .filter(s -> s.getPresupuesto() != null)
                .mapToDouble(Servicio::getPresupuesto).sum();

        double totalPresupuestosAceptados = servicios.stream()
                .filter(s -> "COMPLETADO".equals(s.getEstado()) && s.getPresupuesto() != null)
                .mapToDouble(Servicio::getPresupuesto).sum();
        
        List<String> estadoKeys = new ArrayList<>(serviciosPorEstado.keySet());
        List<Long> estadoValues = new ArrayList<>(serviciosPorEstado.values());
        List<String> tecnicoKeys = new ArrayList<>(serviciosPorTecnico.keySet());
        List<Long> tecnicoValues = new ArrayList<>(serviciosPorTecnico.values());

        // Para los selectores de filtro
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("estados", List.of("PENDIENTE", "ESPERANDO_APROBACION", "PENDIENTE_ASIGNACION_TECNICO", "EN_PROGRESO", "COMPLETADO", "CANCELADO"));

        model.addAttribute("servicios", servicios);
        model.addAttribute("serviciosPorEstado", serviciosPorEstado);
        model.addAttribute("serviciosPorTecnico", serviciosPorTecnico);
        model.addAttribute("totalPresupuestosGenerados", totalPresupuestosGenerados);
        model.addAttribute("totalPresupuestosAceptados", totalPresupuestosAceptados);
        model.addAttribute("totalServicios", servicios.size());

        // Mantener filtros en el modelo para recargar la vista
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("estado", estado);
        model.addAttribute("tecnicoId", tecnicoId);

        
        model.addAttribute("estadoKeys", estadoKeys);
        model.addAttribute("estadoValues", estadoValues);
        model.addAttribute("tecnicoKeys", tecnicoKeys);
        model.addAttribute("tecnicoValues", tecnicoValues);
        return "reportes";
    }

    @GetMapping("/excel")
    public void descargarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long tecnicoId,
            HttpSession session, HttpServletResponse response) throws IOException {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : null;

        List<Servicio> servicios = servicioService.findByFilters(inicio, fin, estado, tecnicoId);

        // Crear libro Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte de Servicios");

        // Encabezados
        String[] headers = {"ID", "N° Orden", "Cliente", "Tipo", "Técnico", "Estado", "Fecha Creación", "Presupuesto", "Moneda"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(crearEstiloEncabezado(workbook));
        }

        // Datos
        int rowNum = 1;
        for (Servicio s : servicios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getIdServicio());
            row.createCell(1).setCellValue(s.getNumeroOrden());
            row.createCell(2).setCellValue(s.getCliente() != null ? s.getCliente().getNombre() + " " + s.getCliente().getApellido() : "");
            row.createCell(3).setCellValue(s.getTipoServicio() != null ? s.getTipoServicio().getNombre() : "");
            row.createCell(4).setCellValue(s.getTecnicoAsignado() != null ? s.getTecnicoAsignado().getNombre() : "");
            row.createCell(5).setCellValue(s.getEstado());
            row.createCell(6).setCellValue(s.getFechaCreacion() != null ? s.getFechaCreacion().toString() : "");
            row.createCell(7).setCellValue(s.getPresupuesto() != null ? s.getPresupuesto() : 0);
            row.createCell(8).setCellValue(s.getMoneda() != null ? s.getMoneda() : "Gs.");
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar respuesta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_servicios.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}