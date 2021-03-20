package pl.ppiwd.exerciseanalyst.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import pl.ppiwd.exerciseanalyst.model.AccelerometerMeas;
import pl.ppiwd.exerciseanalyst.model.AltitudeMeas;
import pl.ppiwd.exerciseanalyst.model.AmbientLightMeas;
import pl.ppiwd.exerciseanalyst.model.GyroscopeMeas;
import pl.ppiwd.exerciseanalyst.model.MagnetometerMeas;
import pl.ppiwd.exerciseanalyst.model.MeasurementInserter;
import pl.ppiwd.exerciseanalyst.model.PressureMeas;
import pl.ppiwd.exerciseanalyst.model.TemperatureMeas;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.persistence.entities.AccelerometerMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.AltitudeMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.AmbientLightMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.GyroscopeMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.MagnetometerMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.PressureMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.TemperatureMeasEntity;

public class MeasurementInsertCommand implements MeasurementInserter<Completable> {

    private MeasurementsDao measurementsDao;
    private long sessionId;
    private List<AccelerometerMeas> accelerometerMeas;
    private List<AltitudeMeas> altitudeMeas;
    private List<AmbientLightMeas> ambientLightMeas;
    private List<GyroscopeMeas> gyroscopeMeas;
    private List<MagnetometerMeas> magnetometerMeas;
    private List<PressureMeas> pressureMeas;
    private List<TemperatureMeas> temperatureMeas;

    private List<AccelerometerMeasEntity> accelerometerMeasEntity;
    private List<AltitudeMeasEntity> altitudeMeasEntity;
    private List<AmbientLightMeasEntity> ambientLightMeasEntity;
    private List<GyroscopeMeasEntity> gyroscopeMeasEntity;
    private List<MagnetometerMeasEntity> magnetometerMeasEntity;
    private List<PressureMeasEntity> pressureMeasEntity;
    private List<TemperatureMeasEntity> temperatureMeasEntities;

    public MeasurementInsertCommand(MeasurementsDao measurementsDao, long sessionId) {
        this.measurementsDao = measurementsDao;
        this.sessionId = sessionId;
        this.accelerometerMeas = new ArrayList<>();
        this.altitudeMeas = new ArrayList<>();
        this.ambientLightMeas = new ArrayList<>();
        this.gyroscopeMeas = new ArrayList<>();
        this.magnetometerMeas = new ArrayList<>();
        this.pressureMeas = new ArrayList<>();
        this.temperatureMeas = new ArrayList<>();
    }

    @Override
    public Completable execute() {
        convertMeasurementsToEntities();
        return Completable.fromCallable(() -> {
            measurementsDao.insertMultipleMeasurements(
                    accelerometerMeasEntity,
                    altitudeMeasEntity,
                    ambientLightMeasEntity,
                    gyroscopeMeasEntity,
                    magnetometerMeasEntity,
                    pressureMeasEntity,
                    temperatureMeasEntities
            );
            return null;
        });
    }

    private void convertMeasurementsToEntities() {
        accelerometerMeasEntity = convertToEntities(accelerometerMeas,
                meas -> new AccelerometerMeasEntity(sessionId, meas.getTimestamp(), meas.getVector()));

        altitudeMeasEntity = convertToEntities(altitudeMeas,
                meas -> new AltitudeMeasEntity(sessionId, meas.getTimestamp(), meas.getValue()));

        ambientLightMeasEntity = convertToEntities(ambientLightMeas,
                meas -> new AmbientLightMeasEntity(sessionId, meas.getTimestamp(), meas.getValue()));

        gyroscopeMeasEntity = convertToEntities(gyroscopeMeas,
                meas -> new GyroscopeMeasEntity(sessionId, meas.getTimestamp(), meas.getVector()));

        magnetometerMeasEntity = convertToEntities(magnetometerMeas,
                meas -> new MagnetometerMeasEntity(sessionId, meas.getTimestamp(), meas.getVector()));

        pressureMeasEntity = convertToEntities(pressureMeas,
                meas -> new PressureMeasEntity(sessionId, meas.getTimestamp(), meas.getValue()));

        temperatureMeasEntities = convertToEntities(temperatureMeas,
                meas -> new TemperatureMeasEntity(sessionId, meas.getTimestamp(), meas.getValue()));
    }

    private <T, R> List<R> convertToEntities(
            List<T> measurements, Function<? super T, ? extends R> converter) {
        return measurements.stream().map(converter).collect(Collectors.toList());
    }

    @Override
    public void addAccelerometerMeas(AccelerometerMeas measurement) {
        accelerometerMeas.add(measurement);
    }

    @Override
    public void addAltitudeMeas(AltitudeMeas measurement) {
        altitudeMeas.add(measurement);
    }

    @Override
    public void addAmbientLightMeas(AmbientLightMeas measurement) {
        ambientLightMeas.add(measurement);
    }

    @Override
    public void addGyroscopeMeas(GyroscopeMeas measurement) {
        gyroscopeMeas.add(measurement);
    }

    @Override
    public void addMagnetometerMeas(MagnetometerMeas measurement) {
        magnetometerMeas.add(measurement);
    }

    @Override
    public void addPressureMeas(PressureMeas measurement) {
        pressureMeas.add(measurement);
    }

    @Override
    public void addTemperatureMeas(TemperatureMeas measurement) {
        temperatureMeas.add(measurement);
    }

}
