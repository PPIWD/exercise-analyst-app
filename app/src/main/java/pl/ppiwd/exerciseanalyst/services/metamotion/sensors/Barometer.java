package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import com.mbientlab.metawear.AsyncDataProducer;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.BarometerBosch;

import java.util.Calendar;
import java.util.List;

import pl.ppiwd.exerciseanalyst.model.AltitudeMeas;
import pl.ppiwd.exerciseanalyst.model.Measurement;
import pl.ppiwd.exerciseanalyst.model.PressureMeas;

public class Barometer extends SensorBase {
    private BarometerBosch barometer;
    private AsyncDataProducer pressureProducer;
    private AsyncDataProducer altitudeProducer;
    private PressureMeas lastPressureMeas;
    private AltitudeMeas lastAltitudeMeas;

    public Barometer(SensorCfg sensorCfg) {
        super(sensorCfg);
    }

    @Override
    public void init(MetaWearBoard board) {
        barometer = board.getModule(BarometerBosch.class);
        if (barometer == null)
            throw new SensorNotFoundException("Barometer not found");
    }

    @Override
    public void stop() {
        if (barometer != null)
            barometer.stop();

        if (pressureProducer != null)
            pressureProducer.stop();

        if (altitudeProducer != null)
            altitudeProducer.stop();
    }

    @Override
    protected void configure(SensorCfg sensorCfg) {
        BarometerCfg cfg = (BarometerCfg) sensorCfg;
        barometer.configure()
                .filterCoeff(cfg.getIirCoeff())
                .pressureOversampling(cfg.getPressureOversampling())
                .standbyTime(cfg.getStandbyTimeMs())
                .commit();

        pressureProducer = barometer.pressure();
        altitudeProducer = barometer.altitude();
    }

    @Override
    protected List<DataProducer> getDataProducers() {
        return List.of(pressureProducer, altitudeProducer);
    }

    @Override
    protected void storeData(Calendar timestamp, Data data, DataProducer dataProducer) {
        if (dataProducer == pressureProducer) {
            Log.d("Barometer::Pressure", timestamp + ": " + data.value(Float.class)
                    + ", thread: " + Thread.currentThread());
            lastPressureMeas = new PressureMeas(
                    timestamp.getTimeInMillis(), data.value(Float.class));
        } else if (dataProducer == altitudeProducer) {
            Log.d("Barometer::Altitude", timestamp + ": " + data.value(Float.class)
                    + ", thread: " + Thread.currentThread());
            lastAltitudeMeas = new AltitudeMeas(
                    timestamp.getTimeInMillis(), data.value(Float.class));
        }
    }

    @Override
    protected void startSensor() {
        barometer.start();
    }

    @Override
    protected void startDataProducer(DataProducer dataProducer) {
        if (dataProducer == pressureProducer) {
            pressureProducer.start();
        } else if (dataProducer == altitudeProducer) {
            altitudeProducer.start();
        }
    }

    @Override
    protected Measurement getLastMeasurement(DataProducer dataProducer) {
        if (dataProducer == pressureProducer)
            return lastPressureMeas;
        else
            return lastAltitudeMeas;
    }
}
