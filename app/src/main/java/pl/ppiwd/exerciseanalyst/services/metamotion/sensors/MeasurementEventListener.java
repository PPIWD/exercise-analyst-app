package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import pl.ppiwd.exerciseanalyst.model.Measurement;

public interface MeasurementEventListener {
    void onMeasurement(Measurement measurement);
}
