package pl.ppiwd.exerciseanalyst.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionEntity;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionWithMeasurements;

@Dao
public interface SessionDao {
    @Insert
    public Single<Long> insertSession(SessionEntity sessionEntity);

    @Transaction
    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    public Single<SessionWithMeasurements> getSessionWithMeasurements(long sessionId);

    @Transaction
    @Query("SELECT * FROM sessions ORDER BY id DESC LIMIT 1")
    public Single<SessionWithMeasurements> getLastSession();
}
