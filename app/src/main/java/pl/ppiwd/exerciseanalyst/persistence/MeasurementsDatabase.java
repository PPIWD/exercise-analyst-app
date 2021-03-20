package pl.ppiwd.exerciseanalyst.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.persistence.dao.AccelerometerDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.AltitudeDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.AmbientLightDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.GyroscopeDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.MagnetometerDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.PressureDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.SessionDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.TemperatureDao;
import pl.ppiwd.exerciseanalyst.persistence.entities.AccelerometerMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.AltitudeMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.AmbientLightMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.GyroscopeMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.MagnetometerMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.PressureMeasEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.TemperatureMeasEntity;

@Database(
        entities = {
                SessionEntity.class,
                AccelerometerMeasEntity.class,
                AltitudeMeasEntity.class,
                AmbientLightMeasEntity.class,
                GyroscopeMeasEntity.class,
                MagnetometerMeasEntity.class,
                PressureMeasEntity.class,
                TemperatureMeasEntity.class
        },
        version = 5,
        exportSchema = true)
public abstract class MeasurementsDatabase extends RoomDatabase {
    private static final Object lock = new Object();
    private static MeasurementsDatabase dbInstance = null;

    public static MeasurementsDatabase getInstance(Context context) {
        MeasurementsDatabase db = dbInstance;
        if (db == null) {
            synchronized (lock) {    // While we were waiting for the lock, another
                db = dbInstance;        // thread may have instantiated the object.
                if (db == null) {
                    db = Room.databaseBuilder(
                            context,
                            MeasurementsDatabase.class,
                            Constants.ROOM_DB_NAME
                    ).fallbackToDestructiveMigration().build();
                    dbInstance = db;
                }
            }
        }
        return db;
    }

    public abstract MeasurementsDao measurementsDao();

    public abstract AccelerometerDao accelerometerDao();

    public abstract AltitudeDao altitudeDao();

    public abstract AmbientLightDao ambientLightDao();

    public abstract GyroscopeDao gyroscopeDao();

    public abstract MagnetometerDao magnetometerDao();

    public abstract PressureDao pressureDao();

    public abstract SessionDao sessionDao();

    public abstract TemperatureDao temperatureDao();
}
