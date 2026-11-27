package com.carmenviboral.eventos.controller;

import com.carmenviboral.eventos.service.ReportService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Controlador REST de informes. Expone las estadísticas de eventos en {@code GET /api/reports}.
 *
 * @author Carmen Viboral
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    /**
     * Crea el controlador. Spring inyecta el servicio automáticamente.
     *
     * @param reportService servicio de informes
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * {@code GET /api/reports} — Devuelve el informe general de eventos
     * (totales, gratuitos, de pago, por categoría y por lugar).
     *
     * @return mapa con las estadísticas, convertido a JSON
     */
    @GetMapping
    public Map<String, Object> getReport() {
        return reportService.getGeneralReport();
    }
}
