package pl.ppiwd.exerciseanalyst.repository;

import java.util.ArrayList;
import java.util.List;

import pl.ppiwd.exerciseanalyst.model.Measurement;

/**
 * Configurable size buffer is introduced, so costly database writes are not executed to often
 */
public class MeasurementsBuffer {

    private final int limit;
    private List<Measurement> measurements;

    public MeasurementsBuffer(int limit) {
        this.limit = limit;
        this.measurements = new ArrayList<>(this.limit);
    }

    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void resetBuffer() {
        measurements = new ArrayList<>(limit);
    }

    public int size() {
        return measurements.size();
    }

    public boolean isFull() {
        return measurements.size() >= limit;
    }
}
