package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

public class SensorNotFoundException extends RuntimeException {
    public SensorNotFoundException(String message) {
        super(message);
    }

    public SensorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
