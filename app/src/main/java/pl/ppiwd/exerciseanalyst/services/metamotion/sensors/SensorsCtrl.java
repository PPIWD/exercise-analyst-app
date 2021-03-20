package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import java.util.Map;

import pl.ppiwd.exerciseanalyst.model.Measurement;
import pl.ppiwd.exerciseanalyst.repository.MeasurementsRepository;

public class SensorsCtrl implements MeasurementEventListener {
    private MeasurementsRepository measurementsRepository;
    private Map<SensorType, SensorBase> sensors;

    public SensorsCtrl(
            SensorsFactory sensorsFactory,
            MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
        sensors = sensorsFactory.make();
    }

    public void startSensors() {
        for (Map.Entry<SensorType, SensorBase> entry : sensors.entrySet()) {
            entry.getValue().setupAndStart(this);
            Log.i("SensorsCtrl", "Sensor " + entry.getKey() + " started");
        }
    }

    public void stopSensors() {
        for (Map.Entry<SensorType, SensorBase> entry : sensors.entrySet()) {
            entry.getValue().stop();
            Log.i("SensorsCtrl", "Sensor " + entry.getKey() + " stopped");
        }
    }

    @Override
    public void onMeasurement(Measurement measurement) {
        measurementsRepository.storeMeasurement(measurement);
    }
}
