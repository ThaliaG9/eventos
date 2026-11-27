package com.carmenviboral.eventos.repository;

import com.carmenviboral.eventos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio de acceso a datos de la tabla {@code user}.
 * <p>
 * Además de las operaciones CRUD heredadas de {@link JpaRepository}, declara dos consultas
 * por correo electrónico que Spring genera a partir del nombre del método.
 *
 * @author Carmen Viboral
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Busca un usuario por su correo electrónico. Se usa en el inicio de sesión.
     *
     * @param email correo a buscar
     * @return el usuario envuelto en un {@link Optional}, o vacío si no existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Comprueba si ya hay un usuario registrado con ese correo.
     * Se usa para impedir correos duplicados al crear usuarios.
     *
     * @param email correo a comprobar
     * @return {@code true} si el correo ya está registrado
     */
    boolean existsByEmail(String email);
}
