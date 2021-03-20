package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.TemperatureMeasEntity;

@Dao
public interface TemperatureDao {
    @Insert
    public Single<Long> insertMeasurement(TemperatureMeasEntity measurement);

    @Insert
    public List<Long> insertTemperatureMeasurements(List<TemperatureMeasEntity> measurement);
}
