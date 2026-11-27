package com.carmenviboral.eventos.service;

import com.carmenviboral.eventos.model.Types;
import com.carmenviboral.eventos.repository.TypesRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio con la lógica de negocio de las categorías de eventos.
 * Hace de intermediario entre {@code TypeController} y {@link TypesRepository}.
 *
 * @author Carmen Viboral
 */
@Service
public class TypeService {

    private final TypesRepository typesRepository;

    /**
     * Crea el servicio. Spring inyecta el repositorio automáticamente.
     *
     * @param typesRepository repositorio de categorías
     */
    public TypeService(TypesRepository typesRepository) {
        this.typesRepository = typesRepository;
    }

    /**
     * Obtiene todas las categorías.
     *
     * @return lista de categorías
     */
    public List<Types> findAll() {
        return typesRepository.findAll();
    }

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return la categoría encontrada
     * @throws RuntimeException si no existe
     */
    public Types findById(Integer id) {
        return typesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    /**
     * Guarda una categoría nueva.
     *
     * @param type categoría a crear
     * @return la categoría guardada con su identificador
     */
    public Types save(Types type) {
        return typesRepository.save(type);
    }

    /**
     * Cambia el nombre de una categoría existente.
     *
     * @param id   identificador de la categoría
     * @param type nuevos datos
     * @return la categoría actualizada
     * @throws RuntimeException si la categoría no existe
     */
    public Types update(Integer id, Types type) {
        Types existing = findById(id);
        existing.setTypeName(type.getTypeName());
        return typesRepository.save(existing);
    }

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id identificador de la categoría
     */
    public void delete(Integer id) {
        typesRepository.deleteById(id);
    }
}
