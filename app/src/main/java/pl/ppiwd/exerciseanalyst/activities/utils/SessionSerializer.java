package pl.ppiwd.exerciseanalyst.activities.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import java.util.Map;
import java.util.Set;

import pl.ppiwd.exerciseanalyst.persistence.entities.AccelerometerMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.GyroscopeMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionWithMeasurements;
import pl.ppiwd.exerciseanalyst.utils.Vector3d;

public class SessionSerializer {
    private SessionWithMeasurements sessionData;
    private Gson gson;

    public SessionSerializer(SessionWithMeasurements sessionData) {
        this.sessionData = sessionData;

        final JsonSerializer<GyroscopeMeasEntity> gyroEntitySerializer = (measurement, typeOfSrc, context) -> {
            JsonObject object = new JsonObject();
            object.addProperty("timestampUtc", measurement.getTimestampUtc());
            JsonElement vectorSerialized = context.serialize(measurement.getVector(), Vector3d.class);
            return addToParent(object, vectorSerialized.getAsJsonObject());
        };

        final JsonSerializer<AccelerometerMeasEntity> accelEntitySerializer = (measurement, typeOfSrc, context) -> {
            JsonObject object = new JsonObject();
            object.addProperty("timestampUtc", measurement.getTimestampUtc());
            JsonElement vectorSerialized = context.serialize(measurement.getVector());
            return addToParent(object, vectorSerialized.getAsJsonObject());
        };

        final JsonSerializer<SessionWithMeasurements> sessionEntitySerializer = (session, typeOfSrc, context) -> {
            JsonObject object = new JsonObject();
            JsonElement sessionSerialized = context.serialize(session.getSessionEntity());
            addToParent(object, sessionSerialized.getAsJsonObject());

            object.add("accelerometerMeasEntities", context.serialize(session.getAccelerometerMeasEntities()));
            object.add("gyroscopeMeasEntities", context.serialize(session.getAccelerometerMeasEntities()));
            return object;
        };

        this.gson = new GsonBuilder()
                .registerTypeAdapter(GyroscopeMeasEntity.class, gyroEntitySerializer)
                .registerTypeAdapter(AccelerometerMeasEntity.class, accelEntitySerializer)
                .registerTypeAdapter(SessionWithMeasurements.class, sessionEntitySerializer)
                .create();
    }

    public String serialize() { return this.gson.toJson(this.sessionData); }

    /**
     * The following function iterates over all attributes of a child
     * object and adds them to the parent object.
     */
    private JsonElement addToParent(JsonObject parent, JsonObject child) {
        Set<Map.Entry<String, JsonElement>> entries = child.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            parent.add(entry.getKey(), entry.getValue());
        }
        return parent;
    }
}
