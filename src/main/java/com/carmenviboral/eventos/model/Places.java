package com.carmenviboral.eventos.model;

import jakarta.persistence.*;

/**
 * Entidad que representa un lugar donde se pueden celebrar eventos.
 * Se corresponde con la tabla {@code places} de la base de datos.
 *
 * @author Carmen Viboral
 */
@Entity
@Table(name = "places")
public class Places {

    /** Identificador único del lugar. Lo genera la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPlaces")
    private Integer idPlaces;

    /** Nombre del lugar (por ejemplo, "Teatro Municipal"). Obligatorio. */
    @Column(name = "placeName", nullable = false, length = 45)
    private String placeName;

    /** Dirección del lugar. Obligatoria. (La columna se llama {@code Adress} en la base de datos.) */
    @Column(name = "Adress", nullable = false, length = 45)
    private String adress;

    /** Constructor vacío, requerido por JPA. */
    public Places() {}

    /**
     * Crea un lugar con su nombre y dirección.
     *
     * @param placeName nombre del lugar
     * @param adress    dirección del lugar
     */
    public Places(String placeName, String adress) {
        this.placeName = placeName;
        this.adress = adress;
    }

    /** @return identificador del lugar */
    public Integer getIdPlaces() { return idPlaces; }
    /** @param idPlaces identificador del lugar */
    public void setIdPlaces(Integer idPlaces) { this.idPlaces = idPlaces; }

    /** @return nombre del lugar */
    public String getPlaceName() { return placeName; }
    /** @param placeName nombre del lugar */
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    /** @return dirección del lugar */
    public String getAdress() { return adress; }
    /** @param adress dirección del lugar */
    public void setAdress(String adress) { this.adress = adress; }
}
