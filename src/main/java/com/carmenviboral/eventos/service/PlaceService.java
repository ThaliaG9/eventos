package com.carmenviboral.eventos.service;

import com.carmenviboral.eventos.model.Places;
import com.carmenviboral.eventos.repository.PlacesRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio con la lógica de negocio de los lugares.
 * Hace de intermediario entre {@code PlaceController} y {@link PlacesRepository}.
 *
 * @author Carmen Viboral
 */
@Service
public class PlaceService {

    private final PlacesRepository placesRepository;

    /**
     * Crea el servicio. Spring inyecta el repositorio automáticamente.
     *
     * @param placesRepository repositorio de lugares
     */
    public PlaceService(PlacesRepository placesRepository) {
        this.placesRepository = placesRepository;
    }

    /**
     * Obtiene todos los lugares.
     *
     * @return lista de lugares
     */
    public List<Places> findAll() {
        return placesRepository.findAll();
    }

    /**
     * Busca un lugar por su identificador.
     *
     * @param id identificador del lugar
     * @return el lugar encontrado
     * @throws RuntimeException si no existe
     */
    public Places findById(Integer id) {
        return placesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado con ID: " + id));
    }

    /**
     * Guarda un lugar nuevo.
     *
     * @param place lugar a crear
     * @return el lugar guardado con su identificador
     */
    public Places save(Places place) {
        return placesRepository.save(place);
    }

    /**
     * Actualiza el nombre y la dirección de un lugar existente.
     *
     * @param id    identificador del lugar
     * @param place nuevos datos
     * @return el lugar actualizado
     * @throws RuntimeException si el lugar no existe
     */
    public Places update(Integer id, Places place) {
        Places existing = findById(id);
        existing.setPlaceName(place.getPlaceName());
        existing.setAdress(place.getAdress());
        return placesRepository.save(existing);
    }

    /**
     * Elimina un lugar por su identificador.
     *
     * @param id identificador del lugar
     */
    public void delete(Integer id) {
        placesRepository.deleteById(id);
    }
}
