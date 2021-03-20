package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import com.mbientlab.metawear.AsyncDataProducer;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.AmbientLightLtr329;

import java.util.Calendar;
import java.util.List;

import pl.ppiwd.exerciseanalyst.model.AmbientLightMeas;
import pl.ppiwd.exerciseanalyst.model.Measurement;

public class AmbientLight extends SensorBase {
    private AmbientLightLtr329 ambientLight;
    private AsyncDataProducer ambientLightDataProducer;
    private AmbientLightMeas lastMeasurement;

    public AmbientLight(SensorCfg sensorCfg) {
        super(sensorCfg);
    }

    @Override
    public void init(MetaWearBoard board) {
        ambientLight = board.getModule(AmbientLightLtr329.class);
        if (ambientLight == null)
            throw new SensorNotFoundException("AmbientLight not found");

        ambientLightDataProducer = ambientLight.illuminance();
    }

    @Override
    public void stop() {
        if (ambientLightDataProducer != null)
            ambientLightDataProducer.stop();
    }


    @Override
    protected void configure(SensorCfg sensorCfg) {
        AmbientLightCfg cfg = (AmbientLightCfg) sensorCfg;
        ambientLight.configure()
                .gain(cfg.getGain())
                .integrationTime(cfg.getIntegrationTime())
                .measurementRate(cfg.getMeasurementRate())
                .commit();
    }

    @Override
    protected List<DataProducer> getDataProducers() {
        return List.of(ambientLightDataProducer);
    }

    @Override
    protected void storeData(Calendar timestamp, Data data, DataProducer dataProducer) {
        if (dataProducer == ambientLightDataProducer) {
            Log.d("AmbientLight", timestamp.getTimeInMillis() + ": "
                    + data.value(Float.class) + ", thread: " + Thread.currentThread());
            lastMeasurement = new AmbientLightMeas(
                    timestamp.getTimeInMillis(), data.value(Float.class));
        }
    }

    @Override
    protected void startSensor() {
    }

    @Override
    protected void startDataProducer(DataProducer dataProducer) {
        if (dataProducer == ambientLightDataProducer)
            ambientLightDataProducer.start();
    }

    @Override
    protected Measurement getLastMeasurement(DataProducer dataProducer) {
        return lastMeasurement;
    }
}
