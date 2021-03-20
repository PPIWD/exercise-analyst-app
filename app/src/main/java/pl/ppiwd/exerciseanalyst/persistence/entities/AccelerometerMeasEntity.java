package pl.ppiwd.exerciseanalyst.persistence.entities;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import pl.ppiwd.exerciseanalyst.utils.Vector3d;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "accelerometer_measurements",
        foreignKeys = {
                @ForeignKey(
                        entity = SessionEntity.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
                        onDelete = CASCADE
                )})
public class AccelerometerMeasEntity {
    @ColumnInfo(index = true)
    private long sessionId;

    @PrimaryKey(autoGenerate = true)
    private long measurementId;

    private long timestampUtc;
    @Embedded
    private Vector3d vector;

    public AccelerometerMeasEntity(long sessionId, long timestampUtc, Vector3d vector) {
        this.sessionId = sessionId;
        this.timestampUtc = timestampUtc;
        this.vector = vector;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(long measurementId) {
        this.measurementId = measurementId;
    }

    public long getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(long timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public Vector3d getVector() {
        return vector;
    }

    public void setVector(Vector3d vector) {
        this.vector = vector;
    }
}
