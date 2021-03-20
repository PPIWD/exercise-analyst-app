package pl.ppiwd.exerciseanalyst.services.metamotion.sensors;

import com.mbientlab.metawear.module.AmbientLightLtr329;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.MagnetometerBmm150;

import java.util.Map;

public class SensorsConfiguration {
    private static final class AccelerometerCfgConst {
        private static final float SAMPLE_RATE = 10.0f;
        private static final float DATA_RANGE = 4.0f;
    }

    private static final class GyroscopeCfgConst {
        private static final GyroBmi160.OutputDataRate ODR = GyroBmi160.OutputDataRate.ODR_25_HZ;
        private static final GyroBmi160.Range DATA_RANGE = GyroBmi160.Range.FSR_1000;
    }

    private static final class MagnetometerCfgConst {
        private static final MagnetometerBmm150.Preset PRESET = MagnetometerBmm150.Preset.REGULAR;
    }

    private static final class ThermometerCfgConst {
        private static final int READING_PERIOD_MS = 150;
    }

    private static final class BarometerCfgConst {
        private static final BarometerBosch.FilterCoeff IIR_COEFF
                = BarometerBosch.FilterCoeff.AVG_16;
        private static final BarometerBosch.OversamplingMode PRESSURE_OVERSAMPLING
                = BarometerBosch.OversamplingMode.ULTRA_HIGH;
        private static final float STANDBY_TIME_MS = 125.0f;
    }

    private static final class AmbientLightCfgConst {
        private static final AmbientLightLtr329.Gain GAIN = AmbientLightLtr329.Gain.LTR329_8X;
        private static final AmbientLightLtr329.IntegrationTime INTEGRATION_TIME
                = AmbientLightLtr329.IntegrationTime.LTR329_TIME_250MS;
        private static final AmbientLightLtr329.MeasurementRate MEASUREMENT_RATE
                = AmbientLightLtr329.MeasurementRate.LTR329_RATE_200MS;
    }

    public static final AccelerometerCfg accelerometerCfg = new AccelerometerCfg(
            AccelerometerCfgConst.SAMPLE_RATE,
            AccelerometerCfgConst.DATA_RANGE
    );

    public static final GyroscopeCfg gyroscopeCfg = new GyroscopeCfg(
            GyroscopeCfgConst.ODR,
            GyroscopeCfgConst.DATA_RANGE
    );

    public static final MagnetometerCfg magnetometerCfg = new MagnetometerCfg(
            MagnetometerCfgConst.PRESET
    );

    public static final ThermometerCfg thermometerCfg = new ThermometerCfg(
            ThermometerCfgConst.READING_PERIOD_MS
    );

    public static final BarometerCfg barometerCfg = new BarometerCfg(
            BarometerCfgConst.IIR_COEFF,
            BarometerCfgConst.PRESSURE_OVERSAMPLING,
            BarometerCfgConst.STANDBY_TIME_MS
    );

    public static final AmbientLightCfg ambientLightCfg = new AmbientLightCfg(
            AmbientLightCfgConst.GAIN,
            AmbientLightCfgConst.INTEGRATION_TIME,
            AmbientLightCfgConst.MEASUREMENT_RATE
    );

    public static final Map<SensorType, SensorCfg> usedSensors = Map.of(
            SensorType.ACCELEROMETER, accelerometerCfg,
            SensorType.GYROSCOPE, gyroscopeCfg,
            SensorType.BAROMETER, barometerCfg,
            SensorType.TEMPERATURE, thermometerCfg,
            SensorType.MAGNETOMETER, magnetometerCfg,
            SensorType.AMBIENT_LIGHT, ambientLightCfg
    );
}
