package com.carmenviboral.eventos.repository;

import com.carmenviboral.eventos.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repositorio de acceso a datos de la tabla {@code events}.
 * <p>
 * Al extender {@link JpaRepository}, Spring Data genera automáticamente las operaciones
 * básicas: {@code findAll}, {@code findById}, {@code save}, {@code deleteById}, etc.
 * Aquí solo se declaran las consultas adicionales que necesita la aplicación.
 *
 * @author Carmen Viboral
 */
public interface EventsRepository extends JpaRepository<Events, Integer> {

    /**
     * Comprueba si ya existe un evento en un lugar, fecha y hora concretos.
     * Spring genera la consulta a partir del nombre del método.
     * Se usa para evitar que dos eventos coincidan en el mismo sitio al mismo tiempo.
     *
     * @param idPlace   identificador del lugar
     * @param eventDate fecha del evento
     * @param eventTime hora del evento
     * @return {@code true} si ya hay un evento con esos datos
     */
    boolean existsByPlace_IdPlacesAndEventDateAndEventTime(Integer idPlace, LocalDate eventDate, LocalTime eventTime);

    /**
     * Filtra eventos combinando varios criterios opcionales.
     * Cada parámetro que llegue como {@code null} se ignora en la consulta.
     *
     * @param idTypes   identificador de la categoría (opcional)
     * @param idPlace   identificador del lugar (opcional)
     * @param startDate fecha mínima, incluida (opcional)
     * @param endDate   fecha máxima, incluida (opcional)
     * @param freeOnly  si es {@code true}, solo devuelve eventos con precio 0
     * @return lista de eventos que cumplen todos los criterios indicados
     */
    @Query("SELECT e FROM Events e WHERE " +
           "(:idTypes IS NULL OR e.type.idTypes = :idTypes) AND " +
           "(:idPlace IS NULL OR e.place.idPlaces = :idPlace) AND " +
           "(:startDate IS NULL OR e.eventDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.eventDate <= :endDate) AND " +
           "(:freeOnly = false OR e.price = 0)")
    List<Events> filterEvents(@Param("idTypes") Integer idTypes,
                              @Param("idPlace") Integer idPlace,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("freeOnly") boolean freeOnly);
}
