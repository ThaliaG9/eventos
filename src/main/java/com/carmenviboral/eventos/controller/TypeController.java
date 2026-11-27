package com.carmenviboral.eventos.controller;

import com.carmenviboral.eventos.model.Types;
import com.carmenviboral.eventos.service.TypeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST de categorías de eventos. Expone el CRUD completo bajo {@code /api/types}.
 *
 * @author Carmen Viboral
 */
@RestController
@RequestMapping("/api/types")
@CrossOrigin(origins = "*")
public class TypeController {

    private final TypeService typeService;

    /**
     * Crea el controlador. Spring inyecta el servicio automáticamente.
     *
     * @param typeService servicio de categorías
     */
    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    /**
     * {@code GET /api/types} — Devuelve todas las categorías.
     *
     * @return lista de categorías
     */
    @GetMapping
    public List<Types> getAll() {
        return typeService.findAll();
    }

    /**
     * {@code GET /api/types/{id}} — Devuelve una categoría concreta.
     *
     * @param id identificador de la categoría
     * @return la categoría encontrada
     */
    @GetMapping("/{id}")
    public Types getById(@PathVariable Integer id) {
        return typeService.findById(id);
    }

    /**
     * {@code POST /api/types} — Crea una categoría.
     *
     * @param type datos de la categoría (cuerpo JSON)
     * @return la categoría creada
     */
    @PostMapping
    public Types create(@RequestBody Types type) {
        return typeService.save(type);
    }

    /**
     * {@code PUT /api/types/{id}} — Actualiza una categoría.
     *
     * @param id   identificador de la categoría
     * @param type nuevos datos (cuerpo JSON)
     * @return la categoría actualizada
     */
    @PutMapping("/{id}")
    public Types update(@PathVariable Integer id, @RequestBody Types type) {
        return typeService.update(id, type);
    }

    /**
     * {@code DELETE /api/types/{id}} — Elimina una categoría.
     *
     * @param id identificador de la categoría
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        typeService.delete(id);
    }
}
