package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.GyroscopeMeasEntity;

@Dao
public interface GyroscopeDao {
    @Insert
    public Single<Long> insertMeasurement(GyroscopeMeasEntity measurement);

    @Insert
    public List<Long> insertGyroscopeMeasurements(List<GyroscopeMeasEntity> measurement);
}
