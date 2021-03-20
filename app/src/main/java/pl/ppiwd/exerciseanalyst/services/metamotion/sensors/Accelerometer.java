package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.data.Acceleration;

import java.util.Calendar;
import java.util.List;

import pl.ppiwd.exerciseanalyst.model.AccelerometerMeas;
import pl.ppiwd.exerciseanalyst.model.Measurement;
import pl.ppiwd.exerciseanalyst.utils.Vector3d;


public class Accelerometer extends SensorBase {
    private com.mbientlab.metawear.module.Accelerometer accelerometer;
    private com.mbientlab.metawear.module.Accelerometer.AccelerationDataProducer
            accelerationDataProducer;
    private AccelerometerMeas lastMeasurement;

    public Accelerometer(SensorCfg sensorCfg) {
        super(sensorCfg);
    }

    @Override
    public void init(MetaWearBoard board) {
        accelerometer = board.getModule(com.mbientlab.metawear.module.Accelerometer.class);
        if (accelerometer == null)
            throw new SensorNotFoundException("Accelerometer not found");
    }

    @Override
    public void stop() {
        if (accelerometer != null)
            accelerometer.stop();
        if (accelerationDataProducer != null)
            accelerationDataProducer.stop();
    }

    @Override
    public void configure(SensorCfg sensorCfg) {
        AccelerometerCfg cfg = (AccelerometerCfg) sensorCfg;
        accelerometer.configure()
                .odr(cfg.getSampleRate())
                .range(cfg.getDataRange())
                .commit();
        accelerationDataProducer = accelerometer.acceleration();
    }

    @Override
    protected List<DataProducer> getDataProducers() {
        return List.of(accelerationDataProducer);
    }

    @Override
    protected void storeData(Calendar timestamp, Data data, DataProducer dataProducer) {
        if (dataProducer == accelerationDataProducer) {
            Acceleration accelerationValue = data.value(Acceleration.class);
            Log.d("Accelerometer", timestamp.getTimeInMillis() + " : " + accelerationValue
                    + ", thread: " + Thread.currentThread());
            lastMeasurement = new AccelerometerMeas(timestamp.getTimeInMillis(),
                    new Vector3d(
                            accelerationValue.x(),
                            accelerationValue.y(),
                            accelerationValue.z()
                    )
            );
        }
    }

    @Override
    protected void startSensor() {
        accelerometer.start();
    }

    @Override
    protected void startDataProducer(DataProducer dataProducer) {
        if (dataProducer == accelerationDataProducer)
            accelerationDataProducer.start();
    }

    @Override
    protected Measurement getLastMeasurement(DataProducer dataProducer) {
        return lastMeasurement;
    }

}
