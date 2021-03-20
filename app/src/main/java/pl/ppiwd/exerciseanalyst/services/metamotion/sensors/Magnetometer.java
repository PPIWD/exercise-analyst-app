package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.data.MagneticField;
import com.mbientlab.metawear.module.MagnetometerBmm150;

import java.util.Calendar;
import java.util.List;

import pl.ppiwd.exerciseanalyst.model.MagnetometerMeas;
import pl.ppiwd.exerciseanalyst.model.Measurement;
import pl.ppiwd.exerciseanalyst.utils.Vector3d;

public class Magnetometer extends SensorBase {
    private MagnetometerBmm150 magnetometer;
    private MagnetometerBmm150.MagneticFieldDataProducer magnetometerDataProducer;
    private MagnetometerMeas lastMeasurement;

    public Magnetometer(SensorCfg sensorCfg) {
        super(sensorCfg);
    }

    @Override
    public void init(MetaWearBoard board) {
        magnetometer = board.getModule(MagnetometerBmm150.class);
        if (magnetometer == null)
            throw new SensorNotFoundException("Magnetometer not found");
    }

    @Override
    public void stop() {
        if (magnetometer != null)
            magnetometer.stop();

        if (magnetometerDataProducer != null)
            magnetometerDataProducer.stop();
    }

    @Override
    protected void configure(SensorCfg sensorCfg) {
        MagnetometerCfg cfg = (MagnetometerCfg) sensorCfg;
        magnetometer.usePreset(cfg.getPreset());
        magnetometerDataProducer = magnetometer.magneticField();
    }

    @Override
    protected List<DataProducer> getDataProducers() {
        return List.of(magnetometerDataProducer);
    }

    @Override
    protected void storeData(Calendar timestamp, Data data, DataProducer dataProducer) {
        if (dataProducer == magnetometerDataProducer) {
            MagneticField magneticField = data.value(MagneticField.class);
            Log.d("Magnetometer", timestamp + ": " + magneticField + ", thread: "
                    + Thread.currentThread());
            lastMeasurement = new MagnetometerMeas(timestamp.getTimeInMillis(),
                    new Vector3d(magneticField.x(), magneticField.y(), magneticField.z()));
        }
    }

    @Override
    protected void startSensor() {
        magnetometer.start();
    }

    @Override
    protected void startDataProducer(DataProducer dataProducer) {
        if (dataProducer == magnetometerDataProducer)
            magnetometerDataProducer.start();
    }

    @Override
    protected Measurement getLastMeasurement(DataProducer dataProducer) {
        return lastMeasurement;
    }
}
