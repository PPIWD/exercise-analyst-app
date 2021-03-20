package pl.ppiwd.exerciseanalyst.persistence.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "pressure_measurements",
        foreignKeys = {
                @ForeignKey(
                        entity = SessionEntity.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
                        onDelete = CASCADE
                )})
public class PressureMeasEntity {
    @ColumnInfo(index = true)
    private long sessionId;

    @PrimaryKey(autoGenerate = true)
    private long measurementId;

    private long timestampUtc;
    private float value;

    public PressureMeasEntity(long sessionId, long timestampUtc, float value) {
        this.sessionId = sessionId;
        this.timestampUtc = timestampUtc;
        this.value = value;
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
