package pl.ppiwd.exerciseanalyst.model;


public class AltitudeMeas extends Measurement {
    private long timestamp;
    private float value;

    public AltitudeMeas(long timestamp, float value) {
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
        inserter.addAltitudeMeas(this);
    }
}
