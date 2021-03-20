package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.AmbientLightMeasEntity;

@Dao
public interface AmbientLightDao {
    @Insert
    public Single<Long> insertMeasurement(AmbientLightMeasEntity measurement);

    @Insert
    public List<Long> insertAmbientLightMeasurements(List<AmbientLightMeasEntity> measurement);
}
