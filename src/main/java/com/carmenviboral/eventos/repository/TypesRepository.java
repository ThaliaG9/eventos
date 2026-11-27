package com.carmenviboral.eventos.repository;

import com.carmenviboral.eventos.model.Types;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de acceso a datos de la tabla {@code types} (categorías de eventos).
 * <p>
 * Hereda de {@link JpaRepository} todas las operaciones CRUD, que Spring Data
 * implementa automáticamente.
 *
 * @author Carmen Viboral
 */
public interface TypesRepository extends JpaRepository<Types, Integer> {}
