package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import androidx.annotation.NonNull;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bolts.Continuation;
import pl.ppiwd.exerciseanalyst.model.Measurement;

public abstract class SensorBase {
    protected SensorCfg sensorCfg;
    protected List<Subscriber> dataSubscribers;
    protected MeasurementEventListener measurementEventListener;

    public SensorBase(SensorCfg sensorCfg) {
        this.sensorCfg = sensorCfg;
        this.dataSubscribers = new ArrayList<>();
    }

    public abstract void init(MetaWearBoard board);

    public abstract void stop();

    public void setupAndStart(@NonNull MeasurementEventListener measurementEventListener) {
        this.measurementEventListener = measurementEventListener;
        configure(sensorCfg);
        startWork();
    }

    protected abstract void configure(SensorCfg sensorCfg);

    protected abstract List<DataProducer> getDataProducers();

    protected abstract void storeData(Calendar timestamp, Data data, DataProducer dataProducer);

    protected abstract void startSensor();

    protected abstract void startDataProducer(DataProducer dataProducer);

    protected abstract Measurement getLastMeasurement(DataProducer dataProducer);

    protected void startWork() {
        for (DataProducer dataProducer : getDataProducers()) {
            Subscriber dataSubscriber = (data, env) -> {
                storeData(data.timestamp(), data, dataProducer);
                measurementEventListener.onMeasurement(getLastMeasurement(dataProducer));
            };
            dataSubscribers.add(dataSubscriber);
            setupDataRoute(dataProducer, dataSubscriber);
        }
        startSensor();
    }

    protected void setupDataRoute(
            DataProducer dataProducer,
            Subscriber subscriber) {
        dataProducer.addRouteAsync(source -> source.stream(subscriber))
                .continueWith((Continuation<Route, Void>) task -> {
                    startDataProducer(dataProducer);
                    return null;
                });
    }
}
