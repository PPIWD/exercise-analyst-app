package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import android.util.Log;

import com.mbientlab.metawear.MetaWearBoard;

import java.util.HashMap;
import java.util.Map;

import pl.ppiwd.exerciseanalyst.services.metamotion.FrontendControl;

public class SensorsFactory {
    private MetaWearBoard board;
    private FrontendControl frontendControl;

    public SensorsFactory(MetaWearBoard board, FrontendControl frontendControl) {
        this.board = board;
        this.frontendControl = frontendControl;
    }

    public Map<SensorType, SensorBase> make() {
        Map<SensorType, SensorBase> sensors = new HashMap<>();
        for (Map.Entry<SensorType, SensorCfg> entry : SensorsConfiguration.usedSensors.entrySet()) {
            try {
                sensors.put(entry.getKey(), makeSensor(entry.getKey(), entry.getValue()));
                Log.i("SensorsFactory", "Created: " + entry.getKey());
            } catch (SensorNotFoundException e) {
                frontendControl.onSensorNotFound(entry.getKey());
            }
        }
        return sensors;
    }

    private SensorBase makeSensor(SensorType sensorType, SensorCfg sensorCfg) {
        SensorBase sensor;
        switch (sensorType) {
            case ACCELEROMETER:
                sensor = new Accelerometer(sensorCfg);
                break;
            case GYROSCOPE:
                sensor = new Gyroscope(sensorCfg);
                break;
            case TEMPERATURE:
                sensor = new Thermometer(sensorCfg);
                break;
            case MAGNETOMETER:
                sensor = new Magnetometer(sensorCfg);
                break;
            case BAROMETER:
                sensor = new Barometer(sensorCfg);
                break;
            case AMBIENT_LIGHT:
                sensor = new AmbientLight(sensorCfg);
                break;
            default:
                throw new SensorNotFoundException("Unknown sensor type: " + sensorType);
        }
        sensor.init(board);
        return sensor;
    }
}
