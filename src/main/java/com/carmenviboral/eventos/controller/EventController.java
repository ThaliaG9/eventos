package com.carmenviboral.eventos.controller;

import com.carmenviboral.eventos.model.Events;
import com.carmenviboral.eventos.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST de eventos. Expone los endpoints bajo {@code /api/events}.
 * <p>
 * Recibe las peticiones HTTP, delega el trabajo en {@link EventService} y devuelve
 * la respuesta en formato JSON. {@code @CrossOrigin("*")} permite que un frontend
 * alojado en otro dominio o puerto pueda llamar a la API.
 *
 * <table border="1">
 *   <caption>Endpoints</caption>
 *   <tr><th>Método</th><th>Ruta</th><th>Acción</th></tr>
 *   <tr><td>GET</td><td>/api/events</td><td>Listar todos</td></tr>
 *   <tr><td>GET</td><td>/api/events/{id}</td><td>Obtener uno</td></tr>
 *   <tr><td>POST</td><td>/api/events</td><td>Crear</td></tr>
 *   <tr><td>PUT</td><td>/api/events/{id}</td><td>Actualizar</td></tr>
 *   <tr><td>DELETE</td><td>/api/events/{id}</td><td>Borrar</td></tr>
 *   <tr><td>GET</td><td>/api/events/filter</td><td>Filtrar</td></tr>
 * </table>
 *
 * @author Carmen Viboral
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    /**
     * Crea el controlador. Spring inyecta el servicio automáticamente.
     *
     * @param eventService servicio de eventos
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * {@code GET /api/events} — Devuelve todos los eventos.
     *
     * @return lista de eventos en JSON
     */
    @GetMapping
    public List<Events> getAll() {
        return eventService.findAll();
    }

    /**
     * {@code GET /api/events/{id}} — Devuelve un evento concreto.
     *
     * @param id identificador del evento (tomado de la URL)
     * @return el evento en JSON
     */
    @GetMapping("/{id}")
    public Events getById(@PathVariable Integer id) {
        return eventService.findById(id);
    }

    /**
     * {@code POST /api/events} — Crea un evento a partir del JSON del cuerpo de la petición.
     *
     * @param event datos del evento (cuerpo JSON)
     * @return 200 OK con el evento creado, o 400 Bad Request con {@code {"message": "..."}}
     *         si falla alguna validación
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Events event) {
        try {
            return ResponseEntity.ok(eventService.save(event));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * {@code PUT /api/events/{id}} — Actualiza un evento existente.
     *
     * @param id    identificador del evento
     * @param event nuevos datos (cuerpo JSON)
     * @return 200 OK con el evento actualizado, o 400 Bad Request con el mensaje de error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Events event) {
        try {
            return ResponseEntity.ok(eventService.update(id, event));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * {@code DELETE /api/events/{id}} — Elimina un evento.
     *
     * @param id identificador del evento
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        eventService.delete(id);
    }

    /**
     * {@code GET /api/events/filter} — Filtra eventos mediante parámetros de la URL, todos opcionales.
     * <p>
     * Ejemplo: {@code /api/events/filter?idTypes=2&startDate=2026-12-01&freeOnly=true}
     *
     * @param idTypes   categoría (opcional)
     * @param idPlace   lugar (opcional)
     * @param startDate fecha desde, formato {@code yyyy-MM-dd} (opcional)
     * @param endDate   fecha hasta, formato {@code yyyy-MM-dd} (opcional)
     * @param freeOnly  {@code true} para ver solo eventos gratuitos (por defecto {@code false})
     * @return lista de eventos que cumplen los filtros
     */
    @GetMapping("/filter")
    public List<Events> filter(
            @RequestParam(required = false) Integer idTypes,
            @RequestParam(required = false) Integer idPlace,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "false") boolean freeOnly) {
        return eventService.filter(idTypes, idPlace, startDate, endDate, freeOnly);
    }
}
