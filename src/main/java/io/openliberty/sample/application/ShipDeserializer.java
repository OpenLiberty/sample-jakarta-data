package io.openliberty.sample.application;

import java.lang.reflect.Type;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

public class ShipDeserializer implements JsonbDeserializer<Ship> {

    @Override
    public Ship deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        String shipName = parser.getString();
        System.out.println("Ship: " + shipName);
        if (shipName.equals("Liberty Saucer")) {
            return new Ship(shipName, Ship.Size.small);
        } else if (shipName.equals("Jakarta Sailboat")) {
            return new Ship(shipName, Ship.Size.small);
        } else if (shipName.equals("Sphere-class Web Ship")) {
            return new Ship(shipName, Ship.Size.large);
        } else {
            return null;
        }

    }
    
}
