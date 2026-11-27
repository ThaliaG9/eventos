package com.carmenviboral.eventos.model;

import jakarta.persistence.*;

/**
 * Entidad que representa un usuario del sistema. Se corresponde con la tabla {@code user}.
 * <p>
 * Un usuario puede tener el rol {@link Role#ADMIN} (administra todo el sistema) o
 * {@link Role#ORGANIZER} (organiza eventos, normalmente asociado a un lugar).
 *
 * @author Carmen Viboral
 */
@Entity
@Table(name = "user")
public class User {

    /** Roles posibles de un usuario dentro de la aplicación. */
    public enum Role {
        /** Administrador con acceso completo. */
        ADMIN,
        /** Organizador de eventos. */
        ORGANIZER
    }

    /** Identificador único del usuario. Lo genera la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUser")
    private Integer idUser;

    /** Nombre del usuario. Obligatorio. */
    @Column(name = "userName", nullable = false, length = 45)
    private String userName;

    /** Correo electrónico. Obligatorio; se usa para iniciar sesión y no puede repetirse. */
    @Column(name = "email", nullable = false, length = 45)
    private String email;

    /** Rol del usuario, guardado como texto ("ADMIN" u "ORGANIZER"). */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    /** Identificador del lugar asociado al usuario (guardado como texto). Opcional. */
    @Column(name = "idPlace", length = 45)
    private String idPlace;

    /** Contraseña del usuario. Obligatoria. */
    @Column(name = "password", nullable = false, length = 45)
    private String password;

    /** Constructor vacío, requerido por JPA. */
    public User() {}

    /**
     * Crea un usuario con todos sus datos.
     *
     * @param userName nombre del usuario
     * @param email    correo electrónico
     * @param role     rol del usuario
     * @param idPlace  identificador del lugar asociado
     * @param password contraseña
     */
    public User(String userName, String email, Role role, String idPlace, String password) {
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.idPlace = idPlace;
        this.password = password;
    }

    /** @return identificador del usuario */
    public Integer getIdUser() { return idUser; }
    /** @param idUser identificador del usuario */
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    /** @return nombre del usuario */
    public String getUserName() { return userName; }
    /** @param userName nombre del usuario */
    public void setUserName(String userName) { this.userName = userName; }

    /** @return correo electrónico */
    public String getEmail() { return email; }
    /** @param email correo electrónico */
    public void setEmail(String email) { this.email = email; }

    /** @return rol del usuario */
    public Role getRole() { return role; }
    /** @param role rol del usuario */
    public void setRole(Role role) { this.role = role; }

    /** @return identificador del lugar asociado */
    public String getIdPlace() { return idPlace; }
    /** @param idPlace identificador del lugar asociado */
    public void setIdPlace(String idPlace) { this.idPlace = idPlace; }

    /** @return contraseña del usuario */
    public String getPassword() { return password; }
    /** @param password contraseña del usuario */
    public void setPassword(String password) { this.password = password; }
}
