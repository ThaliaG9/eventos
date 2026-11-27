package com.carmenviboral.eventos.repository;

import com.carmenviboral.eventos.model.Places;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de acceso a datos de la tabla {@code places}.
 * <p>
 * No declara métodos propios: hereda de {@link JpaRepository} todas las operaciones CRUD
 * (crear, leer, actualizar y borrar), que Spring Data implementa automáticamente.
 *
 * @author Carmen Viboral
 */
public interface PlacesRepository extends JpaRepository<Places, Integer> {}
