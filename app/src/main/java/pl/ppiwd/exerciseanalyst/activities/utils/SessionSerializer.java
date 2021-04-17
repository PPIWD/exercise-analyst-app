package pl.ppiwd.exerciseanalyst.activities.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.ppiwd.exerciseanalyst.persistence.entities.SessionWithMeasurements;

public class SessionSerializer {
    private ExclusionStrategy excludedParams;
    private SessionWithMeasurements sessionData;
    private Gson gson;

    public SessionSerializer(SessionWithMeasurements sessionData) {
        this.sessionData = sessionData;
        this.excludedParams = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes field) {
                if (field.getDeclaringClass() != SessionWithMeasurements.class
                        && (field.getName().equals("sessionId") || field.getName().equals("measurementId"))) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
        this.gson = new GsonBuilder().addSerializationExclusionStrategy(this.excludedParams).create();
    }

    public String serialize() {
        return this.gson.toJson(this.sessionData);
    }
}
