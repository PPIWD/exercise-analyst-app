package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

public class ThermometerCfg extends SensorCfg {
    private int readingPeriodMs;

    public ThermometerCfg(int readingPeriodMs) {
        this.readingPeriodMs = readingPeriodMs;
    }

    public int getReadingPeriodMs() {
        return readingPeriodMs;
    }

    public void setReadingPeriodMs(int readingPeriodMs) {
        this.readingPeriodMs = readingPeriodMs;
    }
}
