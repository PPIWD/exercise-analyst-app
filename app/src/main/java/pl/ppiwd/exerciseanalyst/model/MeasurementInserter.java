package pl.ppiwd.exerciseanalyst.model;

import pl.ppiwd.exerciseanalyst.utils.Command;

public interface MeasurementInserter<R> extends Command<R> {

    void addAccelerometerMeas(AccelerometerMeas measurement);

    void addAltitudeMeas(AltitudeMeas measurement);

    void addAmbientLightMeas(AmbientLightMeas measurement);

    void addGyroscopeMeas(GyroscopeMeas measurement);

    void addMagnetometerMeas(MagnetometerMeas measurement);

    void addPressureMeas(PressureMeas measurement);

    void addTemperatureMeas(TemperatureMeas measurement);
}
