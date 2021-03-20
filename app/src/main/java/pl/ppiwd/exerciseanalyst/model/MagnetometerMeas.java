package pl.ppiwd.exerciseanalyst.model;

import pl.ppiwd.exerciseanalyst.utils.Vector3d;

public class MagnetometerMeas extends Measurement {
    private long timestamp;
    private Vector3d vector;

    public MagnetometerMeas(long timestamp, Vector3d vector) {
        this.timestamp = timestamp;
        this.vector = vector;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Vector3d getVector() {
        return vector;
    }

    @Override
    public void configureInserter(MeasurementInserter<?> inserter) {
        inserter.addMagnetometerMeas(this);
    }
}
