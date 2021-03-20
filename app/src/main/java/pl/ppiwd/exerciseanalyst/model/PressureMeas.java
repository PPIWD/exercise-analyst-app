package pl.ppiwd.exerciseanalyst.model;

public class PressureMeas extends Measurement {
    private long timestamp;
    private float value;

    public PressureMeas(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getValue() {
        return value;
    }

    @Override
    public void configureInserter(MeasurementInserter<?> inserter) {
        inserter.addPressureMeas(this);
    }
}
