package com.carmenviboral.eventos.model;

import jakarta.persistence.*;

/**
 * Entidad que representa una categoría o tipo de evento (por ejemplo, "Concierto", "Teatro", "Taller").
 * Se corresponde con la tabla {@code types} de la base de datos.
 *
 * @author Carmen Viboral
 */
@Entity
@Table(name = "types")
public class Types {

    /** Identificador único de la categoría. Lo genera la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTypes")
    private Integer idTypes;

    /** Nombre de la categoría. Obligatorio, máximo 45 caracteres. */
    @Column(name = "typeName", nullable = false, length = 45)
    private String typeName;

    /** Constructor vacío, requerido por JPA. */
    public Types() {}

    /**
     * Crea una categoría con el nombre indicado.
     *
     * @param typeName nombre de la categoría
     */
    public Types(String typeName) {
        this.typeName = typeName;
    }

    /** @return identificador de la categoría */
    public Integer getIdTypes() { return idTypes; }
    /** @param idTypes identificador de la categoría */
    public void setIdTypes(Integer idTypes) { this.idTypes = idTypes; }

    /** @return nombre de la categoría */
    public String getTypeName() { return typeName; }
    /** @param typeName nombre de la categoría */
    public void setTypeName(String typeName) { this.typeName = typeName; }
}
