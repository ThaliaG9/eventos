package com.carmenviboral.eventos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa un evento y se corresponde con la tabla {@code events} de la base de datos.
 * <p>
 * Cada evento pertenece a una categoría ({@link Types}) y se celebra en un lugar ({@link Places}).
 * Ambas relaciones son de tipo "muchos a uno": muchos eventos pueden compartir la misma
 * categoría o el mismo lugar.
 *
 * @author Carmen Viboral
 */
@Entity
@Table(name = "events")
public class Events {

    /** Identificador único del evento. Lo genera la base de datos (AUTO_INCREMENT). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEvents")
    private Integer idEvents;

    /** Título del evento. Obligatorio, máximo 45 caracteres. */
    @Column(name = "title", nullable = false, length = 45)
    private String title;

    /** Descripción breve del evento. Opcional, máximo 45 caracteres. */
    @Column(name = "description", length = 45)
    private String description;

    /** Fecha en la que se celebra el evento. Obligatoria. */
    @Column(name = "eventDate", nullable = false)
    private LocalDate eventDate;

    /** Hora de inicio del evento. Obligatoria. */
    @Column(name = "eventTime", nullable = false)
    private LocalTime eventTime;

    /** Precio de la entrada. Un valor de 0 indica que el evento es gratuito. */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Categoría del evento (clave foránea {@code idTypes} hacia la tabla {@code types}). */
    @ManyToOne
    @JoinColumn(name = "idTypes", nullable = false)
    private Types type;

    /** Lugar donde se celebra el evento (clave foránea {@code idPlace} hacia la tabla {@code places}). */
    @ManyToOne
    @JoinColumn(name = "idPlace", nullable = false)
    private Places place;

    /** Constructor vacío, requerido por JPA para crear instancias al leer de la base de datos. */
    public Events() {}

    /**
     * Crea un evento con todos sus datos (sin identificador, que lo asigna la base de datos).
     *
     * @param title       título del evento
     * @param description descripción breve
     * @param eventDate   fecha del evento
     * @param eventTime   hora del evento
     * @param price       precio de la entrada (0 si es gratuito)
     * @param type        categoría del evento
     * @param place       lugar donde se celebra
     */
    public Events(String title, String description, LocalDate eventDate, LocalTime eventTime, BigDecimal price, Types type, Places place) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.price = price;
        this.type = type;
        this.place = place;
    }

    /** @return identificador del evento */
    public Integer getIdEvents() { return idEvents; }
    /** @param idEvents identificador del evento */
    public void setIdEvents(Integer idEvents) { this.idEvents = idEvents; }

    /** @return título del evento */
    public String getTitle() { return title; }
    /** @param title título del evento */
    public void setTitle(String title) { this.title = title; }

    /** @return descripción del evento */
    public String getDescription() { return description; }
    /** @param description descripción del evento */
    public void setDescription(String description) { this.description = description; }

    /** @return fecha del evento */
    public LocalDate getEventDate() { return eventDate; }
    /** @param eventDate fecha del evento */
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    /** @return hora del evento */
    public LocalTime getEventTime() { return eventTime; }
    /** @param eventTime hora del evento */
    public void setEventTime(LocalTime eventTime) { this.eventTime = eventTime; }

    /** @return precio de la entrada */
    public BigDecimal getPrice() { return price; }
    /** @param price precio de la entrada */
    public void setPrice(BigDecimal price) { this.price = price; }

    /** @return categoría del evento */
    public Types getType() { return type; }
    /** @param type categoría del evento */
    public void setType(Types type) { this.type = type; }

    /** @return lugar del evento */
    public Places getPlace() { return place; }
    /** @param place lugar del evento */
    public void setPlace(Places place) { this.place = place; }
}
