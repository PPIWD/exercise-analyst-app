package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.PressureMeasEntity;

@Dao
public interface PressureDao {
    @Insert
    public Single<Long> insertMeasurement(PressureMeasEntity measurement);

    @Insert
    public List<Long> insertPressureMeasurements(List<PressureMeasEntity> measurement);
}
