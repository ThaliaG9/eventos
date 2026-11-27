package com.carmenviboral.eventos.service;

import com.carmenviboral.eventos.model.Events;
import com.carmenviboral.eventos.repository.EventsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio que genera estadísticas a partir de los eventos registrados.
 * No tiene tabla propia: calcula los datos a partir de la tabla {@code events}.
 *
 * @author Carmen Viboral
 */
@Service
public class ReportService {

    private final EventsRepository eventsRepository;

    /**
     * Crea el servicio. Spring inyecta el repositorio automáticamente.
     *
     * @param eventsRepository repositorio de eventos
     */
    public ReportService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    /**
     * Genera un informe general con:
     * <ul>
     *   <li>{@code totalEvents}: número total de eventos.</li>
     *   <li>{@code freeEvents}: eventos con precio 0.</li>
     *   <li>{@code paidEvents}: eventos de pago.</li>
     *   <li>{@code eventsByType}: número de eventos por categoría.</li>
     *   <li>{@code eventsByPlace}: número de eventos por lugar.</li>
     * </ul>
     * Se usa un {@link LinkedHashMap} para que el JSON salga en ese mismo orden.
     *
     * @return mapa con los datos del informe, que Spring convierte a JSON
     */
    public Map<String, Object> getGeneralReport() {
        List<Events> all = eventsRepository.findAll();
        long free = all.stream().filter(e -> e.getPrice().compareTo(BigDecimal.ZERO) == 0).count();
        long paid = all.size() - free;

        Map<String, Long> byType = all.stream()
                .collect(Collectors.groupingBy(e -> e.getType().getTypeName(), Collectors.counting()));

        Map<String, Long> byPlace = all.stream()
                .collect(Collectors.groupingBy(e -> e.getPlace().getPlaceName(), Collectors.counting()));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalEvents", all.size());
        report.put("freeEvents", free);
        report.put("paidEvents", paid);
        report.put("eventsByType", byType);
        report.put("eventsByPlace", byPlace);
        return report;
    }
}
