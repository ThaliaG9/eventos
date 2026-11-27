package com.carmenviboral.eventos.controller;

import com.carmenviboral.eventos.model.Places;
import com.carmenviboral.eventos.service.PlaceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST de lugares. Expone el CRUD completo bajo {@code /api/places}.
 *
 * @author Carmen Viboral
 */
@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*")
public class PlaceController {

    private final PlaceService placeService;

    /**
     * Crea el controlador. Spring inyecta el servicio automáticamente.
     *
     * @param placeService servicio de lugares
     */
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    /**
     * {@code GET /api/places} — Devuelve todos los lugares.
     *
     * @return lista de lugares
     */
    @GetMapping
    public List<Places> getAll() {
        return placeService.findAll();
    }

    /**
     * {@code GET /api/places/{id}} — Devuelve un lugar concreto.
     *
     * @param id identificador del lugar
     * @return el lugar encontrado
     */
    @GetMapping("/{id}")
    public Places getById(@PathVariable Integer id) {
        return placeService.findById(id);
    }

    /**
     * {@code POST /api/places} — Crea un lugar.
     *
     * @param place datos del lugar (cuerpo JSON)
     * @return el lugar creado
     */
    @PostMapping
    public Places create(@RequestBody Places place) {
        return placeService.save(place);
    }

    /**
     * {@code PUT /api/places/{id}} — Actualiza un lugar.
     *
     * @param id    identificador del lugar
     * @param place nuevos datos (cuerpo JSON)
     * @return el lugar actualizado
     */
    @PutMapping("/{id}")
    public Places update(@PathVariable Integer id, @RequestBody Places place) {
        return placeService.update(id, place);
    }

    /**
     * {@code DELETE /api/places/{id}} — Elimina un lugar.
     *
     * @param id identificador del lugar
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        placeService.delete(id);
    }
}
