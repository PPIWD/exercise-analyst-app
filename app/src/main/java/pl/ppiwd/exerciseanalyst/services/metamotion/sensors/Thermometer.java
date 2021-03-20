package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.Temperature;
import com.mbientlab.metawear.module.Timer;

import java.util.Calendar;
import java.util.List;

import pl.ppiwd.exerciseanalyst.model.Measurement;
import pl.ppiwd.exerciseanalyst.model.TemperatureMeas;


public class Thermometer extends SensorBase {
    private BarometerBosch barometer;  // thermometer is part of barometer
    private Temperature temperature;
    private Temperature.Sensor tempSensor;
    private Timer timer;
    private Timer.ScheduledTask scheduledTask;
    private ThermometerCfg cfg;
    private TemperatureMeas lastMeas;

    public Thermometer(SensorCfg sensorCfg) {
        super(sensorCfg);
    }

    @Override
    public void init(MetaWearBoard board) {
        temperature = board.getModule(Temperature.class);
        if (temperature == null)
            throw new SensorNotFoundException("Thermometer not found");

        tempSensor = temperature.findSensors(Temperature.SensorType.BOSCH_ENV)[0];
        if (tempSensor == null)
            throw new SensorNotFoundException("BoschThermometer not found");

        barometer = board.getModule(BarometerBosch.class);
        if (barometer == null)
            throw new SensorNotFoundException("BarometerBosch not found");

        timer = board.getModule(Timer.class);
    }

    @Override
    public void stop() {
        if (barometer != null)
            barometer.stop();

        if (scheduledTask != null) {
            scheduledTask.stop();
        }
    }

    @Override
    protected void configure(SensorCfg sensorCfg) {
        cfg = (ThermometerCfg) sensorCfg;
    }

    @Override
    protected List<DataProducer> getDataProducers() {
        return List.of(tempSensor);
    }

    @Override
    protected void storeData(Calendar timestamp, Data data, DataProducer dataProducer) {
        if (dataProducer == tempSensor) {
            Log.d("Thermometer", timestamp.getTimeInMillis() + ", value=" +
                    data.value(Float.class) + ", thread: " + Thread.currentThread());
            lastMeas = new TemperatureMeas(timestamp.getTimeInMillis(), data.value(Float.class));
        }
    }

    @Override
    protected void startSensor() {
        barometer.start();
    }

    @Override
    protected void startDataProducer(DataProducer dataProducer) {
        if (dataProducer == tempSensor) {
            timer.scheduleAsync(cfg.getReadingPeriodMs(), false, tempSensor::read)
                    .continueWithTask(task -> {
                        scheduledTask = task.getResult();
                        scheduledTask.start();
                        return null;
                    });
        }
    }

    @Override
    protected Measurement getLastMeasurement(DataProducer dataProducer) {
        return lastMeas;
    }
}
