package io.openliberty.sample.application;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonbTypeDeserializer(ShipDeserializer.class)
public class Ship {
    public static enum Size {
        small, large
    }

    public String shipName;

    public Size size;

    Ship() {
        
    }

    Ship(String name, Size size) {
        this.shipName = name;
        this.size = size;
    }

}
