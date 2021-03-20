package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.MagnetometerMeasEntity;

@Dao
public interface MagnetometerDao {
    @Insert
    public Single<Long> insertMeasurement(MagnetometerMeasEntity measurement);

    @Insert
    public List<Long> insertMagnetometerMeasurements(List<MagnetometerMeasEntity> measurement);
}
