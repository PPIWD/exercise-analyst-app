package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import com.mbientlab.metawear.module.BarometerBosch;

public class BarometerCfg extends SensorCfg {
    private BarometerBosch.FilterCoeff iirCoeff;
    private BarometerBosch.OversamplingMode pressureOversampling;
    private float standbyTimeMs;

    public BarometerCfg(
            BarometerBosch.FilterCoeff iirCoeff,
            BarometerBosch.OversamplingMode pressureOversampling,
            float standbyTimeMs) {
        this.iirCoeff = iirCoeff;
        this.pressureOversampling = pressureOversampling;
        this.standbyTimeMs = standbyTimeMs;
    }

    public BarometerBosch.FilterCoeff getIirCoeff() {
        return iirCoeff;
    }

    public void setIirCoeff(BarometerBosch.FilterCoeff iirCoeff) {
        this.iirCoeff = iirCoeff;
    }

    public BarometerBosch.OversamplingMode getPressureOversampling() {
        return pressureOversampling;
    }

    public void setPressureOversampling(BarometerBosch.OversamplingMode pressureOversampling) {
        this.pressureOversampling = pressureOversampling;
    }

    public float getStandbyTimeMs() {
        return standbyTimeMs;
    }

    public void setStandbyTimeMs(float standbyTimeMs) {
        this.standbyTimeMs = standbyTimeMs;
    }
}
