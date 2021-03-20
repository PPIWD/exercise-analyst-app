package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.GyroBmi160;

import java.util.Calendar;
import java.util.List;

import pl.ppiwd.exerciseanalyst.model.GyroscopeMeas;
import pl.ppiwd.exerciseanalyst.model.Measurement;
import pl.ppiwd.exerciseanalyst.utils.Vector3d;

public class Gyroscope extends SensorBase {
    private GyroBmi160 gyro;
    private GyroBmi160.AngularVelocityDataProducer gyroDataProducer;
    private GyroscopeMeas lastMeasurement;

    public Gyroscope(SensorCfg sensorCfg) {
        super(sensorCfg);
    }

    @Override
    public void init(MetaWearBoard board) {
        gyro = board.getModule(GyroBmi160.class);
        if (gyro == null)
            throw new SensorNotFoundException("Gyroscope not found");
    }

    @Override
    public void stop() {
        if (gyro != null)
            gyro.stop();

        if (gyroDataProducer != null)
            gyroDataProducer.stop();
    }

    @Override
    protected void configure(SensorCfg sensorCfg) {
        GyroscopeCfg cfg = (GyroscopeCfg) sensorCfg;
        gyro.configure()
                .odr(cfg.getOdr())
                .range(cfg.getDataRange())
                .commit();

        gyroDataProducer = gyro.angularVelocity();
    }

    @Override
    protected List<DataProducer> getDataProducers() {
        return List.of(gyroDataProducer);
    }

    @Override
    protected void storeData(Calendar timestamp, Data data, DataProducer dataProducer) {
        if (dataProducer == gyroDataProducer) {
            AngularVelocity angularVelocity = data.value(AngularVelocity.class);
            Log.d("Gyroscope", timestamp + ": " + angularVelocity + ", thread: "
                    + Thread.currentThread());
            lastMeasurement = new GyroscopeMeas(timestamp.getTimeInMillis(),
                    new Vector3d(angularVelocity.x(), angularVelocity.y(), angularVelocity.z()));
        }
    }

    @Override
    protected void startSensor() {
        gyro.start();
    }

    @Override
    protected void startDataProducer(DataProducer dataProducer) {
        if (dataProducer == gyroDataProducer)
            gyroDataProducer.start();
    }

    @Override
    protected Measurement getLastMeasurement(DataProducer dataProducer) {
        return lastMeasurement;
    }
}
