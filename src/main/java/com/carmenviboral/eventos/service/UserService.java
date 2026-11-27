package com.carmenviboral.eventos.service;

import com.carmenviboral.eventos.model.User;
import com.carmenviboral.eventos.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio con la lógica de negocio de los usuarios: CRUD, control de correos
 * duplicados e inicio de sesión.
 *
 * @author Carmen Viboral
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Crea el servicio. Spring inyecta el repositorio automáticamente.
     *
     * @param userRepository repositorio de usuarios
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de usuarios
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return el usuario encontrado
     * @throws RuntimeException si no existe
     */
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Registra un usuario nuevo, comprobando antes que el correo no esté ya en uso.
     *
     * @param user usuario a crear
     * @return el usuario guardado con su identificador
     * @throws RuntimeException si ya existe un usuario con ese correo
     */
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Ya existe un usuario registrado con ese correo.");
        }
        return userRepository.save(user);
    }

    /**
     * Actualiza los datos de un usuario. La contraseña solo se cambia si se envía
     * una nueva (no nula y no vacía); si no, se conserva la anterior.
     *
     * @param id   identificador del usuario
     * @param user nuevos datos
     * @return el usuario actualizado
     * @throws RuntimeException si el usuario no existe
     */
    public User update(Integer id, User user) {
        User existing = findById(id);
        existing.setUserName(user.getUserName());
        existing.setEmail(user.getEmail());
        existing.setRole(user.getRole());
        existing.setIdPlace(user.getIdPlace());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(user.getPassword());
        }
        return userRepository.save(existing);
    }

    /**
     * Elimina un usuario por su identificador.
     *
     * @param id identificador del usuario
     */
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    /**
     * Comprueba las credenciales de un usuario.
     * <p>
     * Si el correo no existe o la contraseña no coincide, devuelve el mismo mensaje
     * genérico para no revelar cuál de los dos datos es incorrecto. Si el acceso es
     * correcto, se borra la contraseña del objeto devuelto para no enviarla al cliente.
     *
     * @param email    correo electrónico
     * @param password contraseña
     * @return el usuario autenticado (sin contraseña)
     * @throws RuntimeException si las credenciales no son válidas
     */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos."));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Correo o contraseña incorrectos.");
        }
        user.setPassword(null);
        return user;
    }
}
