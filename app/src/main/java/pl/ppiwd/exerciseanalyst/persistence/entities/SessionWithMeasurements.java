package pl.ppiwd.exerciseanalyst.persistence.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SessionWithMeasurements {
    @Embedded
    private SessionEntity sessionEntity;

    @Relation(
            parentColumn = "id",
            entityColumn = "sessionId",
            entity = AccelerometerMeasEntity.class
    )
    private List<AccelerometerMeasEntity> accelerometerMeasEntities;

    @Relation(
            parentColumn = "id",
            entityColumn = "sessionId",
            entity = GyroscopeMeasEntity.class
    )
    private List<GyroscopeMeasEntity> gyroscopeMeasEntities;

    @Relation(
            parentColumn = "id",
            entityColumn = "sessionId",
            entity = AltitudeMeasEntity.class
    )
    private List<AltitudeMeasEntity> altitudeMeasEntities;

    @Relation(
            parentColumn = "id",
            entityColumn = "sessionId",
            entity = PressureMeasEntity.class
    )
    private List<PressureMeasEntity> pressureMeasEntities;

    @Relation(
            parentColumn = "id",
            entityColumn = "sessionId",
            entity = MagnetometerMeasEntity.class
    )
    private List<MagnetometerMeasEntity> magnetometerMeasEntities;

    @Relation(
            parentColumn = "id",
            entityColumn = "sessionId",
            entity = AmbientLightMeasEntity.class
    )
    private List<AmbientLightMeasEntity> ambientLightMeasEntities;

    @Relation(
            parentColumn = "id",
            entityColumn = "sessionId",
            entity = TemperatureMeasEntity.class
    )
    private List<TemperatureMeasEntity> temperatureMeasEntities;

    public SessionWithMeasurements(
            SessionEntity sessionEntity,
            List<AccelerometerMeasEntity> accelerometerMeasEntities,
            List<GyroscopeMeasEntity> gyroscopeMeasEntities,
            List<AltitudeMeasEntity> altitudeMeasEntities,
            List<PressureMeasEntity> pressureMeasEntities,
            List<MagnetometerMeasEntity> magnetometerMeasEntities,
            List<AmbientLightMeasEntity> ambientLightMeasEntities,
            List<TemperatureMeasEntity> temperatureMeasEntities) {
        this.sessionEntity = sessionEntity;
        this.accelerometerMeasEntities = accelerometerMeasEntities;
        this.gyroscopeMeasEntities = gyroscopeMeasEntities;
        this.altitudeMeasEntities = altitudeMeasEntities;
        this.pressureMeasEntities = pressureMeasEntities;
        this.magnetometerMeasEntities = magnetometerMeasEntities;
        this.ambientLightMeasEntities = ambientLightMeasEntities;
        this.temperatureMeasEntities = temperatureMeasEntities;
    }

    public SessionEntity getSessionEntity() {
        return sessionEntity;
    }

    public void setSessionEntity(SessionEntity sessionEntity) {
        this.sessionEntity = sessionEntity;
    }

    public List<AccelerometerMeasEntity> getAccelerometerMeasEntities() {
        return accelerometerMeasEntities;
    }

    public void setAccelerometerMeasEntities(List<AccelerometerMeasEntity> accelerometerMeasEntities) {
        this.accelerometerMeasEntities = accelerometerMeasEntities;
    }

    public List<GyroscopeMeasEntity> getGyroscopeMeasEntities() {
        return gyroscopeMeasEntities;
    }

    public void setGyroscopeMeasEntities(List<GyroscopeMeasEntity> gyroscopeMeasEntities) {
        this.gyroscopeMeasEntities = gyroscopeMeasEntities;
    }

    public List<AltitudeMeasEntity> getAltitudeMeasEntities() {
        return altitudeMeasEntities;
    }

    public void setAltitudeMeasEntities(List<AltitudeMeasEntity> altitudeMeasEntities) {
        this.altitudeMeasEntities = altitudeMeasEntities;
    }

    public List<PressureMeasEntity> getPressureMeasEntities() {
        return pressureMeasEntities;
    }

    public void setPressureMeasEntities(List<PressureMeasEntity> pressureMeasEntities) {
        this.pressureMeasEntities = pressureMeasEntities;
    }

    public List<MagnetometerMeasEntity> getMagnetometerMeasEntities() {
        return magnetometerMeasEntities;
    }

    public void setMagnetometerMeasEntities(List<MagnetometerMeasEntity> magnetometerMeasEntities) {
        this.magnetometerMeasEntities = magnetometerMeasEntities;
    }

    public List<AmbientLightMeasEntity> getAmbientLightMeasEntities() {
        return ambientLightMeasEntities;
    }

    public void setAmbientLightMeasEntities(List<AmbientLightMeasEntity> ambientLightMeasEntities) {
        this.ambientLightMeasEntities = ambientLightMeasEntities;
    }

    public List<TemperatureMeasEntity> getTemperatureMeasEntities() {
        return temperatureMeasEntities;
    }

    public void setTemperatureMeasEntities(List<TemperatureMeasEntity> temperatureMeasEntities) {
        this.temperatureMeasEntities = temperatureMeasEntities;
    }
}
