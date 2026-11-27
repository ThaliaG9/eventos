package com.carmenviboral.eventos.controller;

import com.carmenviboral.eventos.model.User;
import com.carmenviboral.eventos.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST de usuarios. Expone el CRUD bajo {@code /api/users} y además
 * el endpoint de inicio de sesión {@code POST /api/users/login}.
 *
 * @author Carmen Viboral
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * Crea el controlador. Spring inyecta el servicio automáticamente.
     *
     * @param userService servicio de usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code GET /api/users} — Devuelve todos los usuarios.
     *
     * @return lista de usuarios
     */
    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    /**
     * {@code GET /api/users/{id}} — Devuelve un usuario concreto.
     *
     * @param id identificador del usuario
     * @return el usuario encontrado
     */
    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    /**
     * {@code POST /api/users} — Registra un usuario nuevo.
     *
     * @param user datos del usuario (cuerpo JSON)
     * @return 200 OK con el usuario creado, o 400 Bad Request si el correo ya existe
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.save(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * {@code PUT /api/users/{id}} — Actualiza un usuario.
     *
     * @param id   identificador del usuario
     * @param user nuevos datos (cuerpo JSON)
     * @return el usuario actualizado
     */
    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @RequestBody User user) {
        return userService.update(id, user);
    }

    /**
     * {@code DELETE /api/users/{id}} — Elimina un usuario.
     *
     * @param id identificador del usuario
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }

    /**
     * {@code POST /api/users/login} — Inicia sesión.
     * <p>
     * Cuerpo esperado: {@code {"email": "...", "password": "..."}}
     *
     * @param credentials mapa con las claves {@code email} y {@code password}
     * @return 200 OK con el usuario (sin contraseña), o 401 Unauthorized con el mensaje de error
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            User loggedUser = userService.login(credentials.get("email"), credentials.get("password"));
            return ResponseEntity.ok(loggedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
