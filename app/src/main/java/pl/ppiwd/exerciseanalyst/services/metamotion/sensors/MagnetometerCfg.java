package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import com.mbientlab.metawear.module.MagnetometerBmm150;

public class MagnetometerCfg extends SensorCfg {
    private MagnetometerBmm150.Preset preset;

    public MagnetometerCfg(MagnetometerBmm150.Preset preset) {
        this.preset = preset;
    }

    public MagnetometerBmm150.Preset getPreset() {
        return preset;
    }

    public void setPreset(MagnetometerBmm150.Preset preset) {
        this.preset = preset;
    }
}
