package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.AltitudeMeasEntity;

@Dao
public interface AltitudeDao {
    @Insert
    public Single<Long> insertMeasurement(AltitudeMeasEntity measurement);

    @Insert
    public List<Long> insertAltitudeMeasurements(List<AltitudeMeasEntity> measurement);
}
