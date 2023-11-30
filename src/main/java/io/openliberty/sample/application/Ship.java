package io.openliberty.sample.application;

import java.io.Serializable;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;

@JsonbTypeDeserializer(ShipDeserializer.class)
public class Ship implements Serializable {
    public static enum Size {
        small, large
    }

    public String name;

    public Size size;

    Ship(String name, Size size) {
        this.name = name;
        this.size = size;
    }

}
