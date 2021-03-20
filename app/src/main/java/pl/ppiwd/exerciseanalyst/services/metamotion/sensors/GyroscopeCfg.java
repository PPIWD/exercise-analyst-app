package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import com.mbientlab.metawear.module.GyroBmi160;

public class GyroscopeCfg extends SensorCfg {
    private GyroBmi160.OutputDataRate odr;
    private GyroBmi160.Range dataRange;

    public GyroscopeCfg(GyroBmi160.OutputDataRate odr, GyroBmi160.Range dataRange) {
        this.odr = odr;
        this.dataRange = dataRange;
    }

    public GyroBmi160.OutputDataRate getOdr() {
        return odr;
    }

    public void setOdr(GyroBmi160.OutputDataRate odr) {
        this.odr = odr;
    }

    public GyroBmi160.Range getDataRange() {
        return dataRange;
    }

    public void setDataRange(GyroBmi160.Range dataRange) {
        this.dataRange = dataRange;
    }
}
