package pl.ppiwd.exerciseanalyst.services.metamotion;

import pl.ppiwd.exerciseanalyst.services.metamotion.sensors.SensorType;

public interface FrontendControl {
    void onMetaWearConnectionSuccess(long sessionId);

    void onMetaWearConnectionError();

    void onSensorNotFound(SensorType sensorType);
}
