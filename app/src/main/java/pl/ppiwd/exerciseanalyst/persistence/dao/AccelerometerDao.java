package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.AccelerometerMeasEntity;

@Dao
public interface AccelerometerDao {
    @Insert
    public Single<Long> insertMeasurement(AccelerometerMeasEntity measurement);

    @Insert
    public List<Long> insertAccelerometerMeasurements(List<AccelerometerMeasEntity> measurements);
}
