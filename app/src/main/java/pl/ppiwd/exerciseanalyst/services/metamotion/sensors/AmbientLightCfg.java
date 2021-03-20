package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import com.mbientlab.metawear.module.AmbientLightLtr329;

public class AmbientLightCfg extends SensorCfg {
    private AmbientLightLtr329.Gain gain;
    private AmbientLightLtr329.IntegrationTime integrationTime;
    private AmbientLightLtr329.MeasurementRate measurementRate;

    public AmbientLightCfg(
            AmbientLightLtr329.Gain gain,
            AmbientLightLtr329.IntegrationTime integrationTime,
            AmbientLightLtr329.MeasurementRate measurementRate) {
        this.gain = gain;
        this.integrationTime = integrationTime;
        this.measurementRate = measurementRate;
    }

    public AmbientLightLtr329.Gain getGain() {
        return gain;
    }

    public void setGain(AmbientLightLtr329.Gain gain) {
        this.gain = gain;
    }

    public AmbientLightLtr329.IntegrationTime getIntegrationTime() {
        return integrationTime;
    }

    public void setIntegrationTime(AmbientLightLtr329.IntegrationTime integrationTime) {
        this.integrationTime = integrationTime;
    }

    public AmbientLightLtr329.MeasurementRate getMeasurementRate() {
        return measurementRate;
    }

    public void setMeasurementRate(AmbientLightLtr329.MeasurementRate measurementRate) {
        this.measurementRate = measurementRate;
    }
}
