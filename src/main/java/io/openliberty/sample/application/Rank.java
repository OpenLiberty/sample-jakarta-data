package io.openliberty.sample.application;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;

@JsonbTypeDeserializer(RankDeserializer.class)
public enum Rank {
    CAPTAIN("Captain"),
    OFFICER("Officer"),
    ENGINEER("Engineer");

    private final String formatted;

    Rank(String s) {
        formatted = s;
    }

    public static Rank fromString(String s) {
        for (Rank r : values()) {
            if (r.formatted.equals(s)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return formatted;
    }

}
