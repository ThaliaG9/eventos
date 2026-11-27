package com.carmenviboral.eventos.service;

import com.carmenviboral.eventos.model.Events;
import com.carmenviboral.eventos.model.Places;
import com.carmenviboral.eventos.model.Types;
import com.carmenviboral.eventos.repository.EventsRepository;
import com.carmenviboral.eventos.repository.PlacesRepository;
import com.carmenviboral.eventos.repository.TypesRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio con la lógica de negocio de los eventos.
 * <p>
 * Además del CRUD, aplica las reglas de validación antes de guardar:
 * <ul>
 *   <li>El precio no puede ser negativo.</li>
 *   <li>La fecha no puede estar en el pasado.</li>
 *   <li>La categoría y el lugar indicados deben existir.</li>
 *   <li>No puede haber dos eventos en el mismo lugar, fecha y hora (solo al crear).</li>
 * </ul>
 *
 * @author Carmen Viboral
 */
@Service
public class EventService {

    private final EventsRepository eventsRepository;
    private final TypesRepository typesRepository;
    private final PlacesRepository placesRepository;

    /**
     * Crea el servicio. Spring inyecta automáticamente los repositorios necesarios.
     *
     * @param eventsRepository repositorio de eventos
     * @param typesRepository  repositorio de categorías
     * @param placesRepository repositorio de lugares
     */
    public EventService(EventsRepository eventsRepository,
                        TypesRepository typesRepository,
                        PlacesRepository placesRepository) {
        this.eventsRepository = eventsRepository;
        this.typesRepository = typesRepository;
        this.placesRepository = placesRepository;
    }

    /**
     * Obtiene todos los eventos.
     *
     * @return lista con todos los eventos registrados
     */
    public List<Events> findAll() {
        return eventsRepository.findAll();
    }

    /**
     * Busca un evento por su identificador.
     *
     * @param id identificador del evento
     * @return el evento encontrado
     * @throws RuntimeException si no existe un evento con ese ID
     */
    public Events findById(Integer id) {
        return eventsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
    }

    /**
     * Valida y guarda un evento nuevo.
     *
     * @param event evento a crear
     * @return el evento guardado, ya con su identificador asignado
     * @throws RuntimeException si no supera alguna de las validaciones
     */
    public Events save(Events event) {
        validateEvent(event, true);
        return eventsRepository.save(event);
    }

    /**
     * Actualiza un evento existente con los datos recibidos.
     *
     * @param id    identificador del evento a modificar
     * @param event nuevos datos del evento
     * @return el evento actualizado
     * @throws RuntimeException si el evento no existe o los datos no son válidos
     */
    public Events update(Integer id, Events event) {
        Events existing = findById(id);
        validateEvent(event, false);

        existing.setTitle(event.getTitle());
        existing.setDescription(event.getDescription());
        existing.setEventDate(event.getEventDate());
        existing.setEventTime(event.getEventTime());
        existing.setPrice(event.getPrice());
        existing.setType(event.getType());
        existing.setPlace(event.getPlace());

        return eventsRepository.save(existing);
    }

    /**
     * Elimina un evento por su identificador.
     *
     * @param id identificador del evento a borrar
     */
    public void delete(Integer id) {
        eventsRepository.deleteById(id);
    }

    /**
     * Filtra eventos por categoría, lugar, rango de fechas y/o gratuidad.
     * Los parámetros nulos no se tienen en cuenta.
     *
     * @param idTypes   identificador de la categoría (opcional)
     * @param idPlace   identificador del lugar (opcional)
     * @param startDate fecha mínima (opcional)
     * @param endDate   fecha máxima (opcional)
     * @param freeOnly  si es {@code true}, solo eventos gratuitos
     * @return lista de eventos que cumplen los filtros
     */
    public List<Events> filter(Integer idTypes, Integer idPlace, LocalDate startDate, LocalDate endDate, boolean freeOnly) {
        return eventsRepository.filterEvents(idTypes, idPlace, startDate, endDate, freeOnly);
    }

    /**
     * Comprueba las reglas de negocio de un evento y sustituye la categoría y el lugar
     * recibidos (que pueden traer solo el ID) por los objetos completos de la base de datos.
     *
     * @param event evento a validar
     * @param isNew {@code true} si es una creación; en ese caso también se comprueba
     *              que no haya otro evento en el mismo lugar, fecha y hora
     * @throws RuntimeException con un mensaje explicativo si alguna regla no se cumple
     */
    private void validateEvent(Events event, boolean isNew) {
        if (event.getPrice() != null && event.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio del evento no puede ser negativo.");
        }
        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha del evento no puede estar en el pasado.");
        }

        Types type = typesRepository.findById(event.getType().getIdTypes())
                .orElseThrow(() -> new RuntimeException("La categoría seleccionada no existe."));
        Places place = placesRepository.findById(event.getPlace().getIdPlaces())
                .orElseThrow(() -> new RuntimeException("El lugar seleccionado no existe."));

        if (isNew && eventsRepository.existsByPlace_IdPlacesAndEventDateAndEventTime(
                place.getIdPlaces(), event.getEventDate(), event.getEventTime())) {
            throw new RuntimeException("Ya existe un evento programado en ese lugar, fecha y hora.");
        }

        event.setType(type);
        event.setPlace(place);
    }
}
