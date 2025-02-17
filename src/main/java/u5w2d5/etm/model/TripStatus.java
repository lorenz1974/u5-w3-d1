// src/main/java/u5w2d5/etm/model/TripStatus.java
package u5w2d5.etm.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TripStatus {
    SCHEDULED("Programmato"),
    IN_PROGRESS("In Corso"),
    COMPLETED("Completato"),
    CANCELLED("Annullato");

    private final String description;

    TripStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
