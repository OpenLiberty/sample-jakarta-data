package io.openliberty.sample.application;

import java.lang.reflect.Type;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;


public class RankDeserializer implements JsonbDeserializer<Rank> {

    @Override
    public Rank deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        String name = parser.getValue().toString().replaceAll("\"", "");
        return Rank.fromString(name);
    }
    
}