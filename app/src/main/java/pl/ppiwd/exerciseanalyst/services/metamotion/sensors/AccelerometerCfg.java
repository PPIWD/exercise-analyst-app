package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

public class AccelerometerCfg extends SensorCfg {
    private float sampleRate;
    private float dataRange;

    public AccelerometerCfg(float sampleRate, float dataRange) {
        this.sampleRate = sampleRate;
        this.dataRange = dataRange;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public float getDataRange() {
        return dataRange;
    }

    public void setDataRange(float dataRange) {
        this.dataRange = dataRange;
    }
}