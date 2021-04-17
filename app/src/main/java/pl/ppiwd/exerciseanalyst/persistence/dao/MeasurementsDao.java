package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.AccelerometerMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.AltitudeMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.AmbientLightMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.GyroscopeMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.MagnetometerMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.PressureMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionWithMeasurements;
import pl.ppiwd.exerciseanalyst.persistence.entities.TemperatureMeasEntity;

@Dao
public abstract class MeasurementsDao implements AccelerometerDao, AltitudeDao, AmbientLightDao, GyroscopeDao, MagnetometerDao, PressureDao, TemperatureDao, SessionDao {

    @Transaction
    public void insertMultipleMeasurements(
            List<AccelerometerMeasEntity> accelerometerMeasEntities,
            List<AltitudeMeasEntity> altitudeMeasEntities,
            List<AmbientLightMeasEntity> ambientLightMeasEntities,
            List<GyroscopeMeasEntity> gyroscopeMeasEntities,
            List<MagnetometerMeasEntity> magnetometerMeasEntities,
            List<PressureMeasEntity> pressureMeasEntities,
            List<TemperatureMeasEntity> temperatureMeasEntities) {
        if (accelerometerMeasEntities != null && accelerometerMeasEntities.size() > 0)
            insertAccelerometerMeasurements(accelerometerMeasEntities);
        if (altitudeMeasEntities != null && altitudeMeasEntities.size() > 0)
            insertAltitudeMeasurements(altitudeMeasEntities);
        if (ambientLightMeasEntities != null && ambientLightMeasEntities.size() > 0)
            insertAmbientLightMeasurements(ambientLightMeasEntities);
        if (gyroscopeMeasEntities != null && gyroscopeMeasEntities.size() > 0)
            insertGyroscopeMeasurements(gyroscopeMeasEntities);
        if (magnetometerMeasEntities != null && magnetometerMeasEntities.size() > 0)
            insertMagnetometerMeasurements(magnetometerMeasEntities);
        if (pressureMeasEntities != null && pressureMeasEntities.size() > 0)
            insertPressureMeasurements(pressureMeasEntities);
        if (temperatureMeasEntities != null && temperatureMeasEntities.size() > 0)
            insertTemperatureMeasurements(temperatureMeasEntities);
    }

    public Single<SessionWithMeasurements> getMeasurementEntitiesForLastSession() {
        return getLastSession();
    }
}
