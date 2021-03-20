package pl.ppiwd.exerciseanalyst.persistence.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class SessionEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String activity;
    private int repetitions;

    public SessionEntity(String activity, int repetitions) {
        this.id = 0;
        this.activity = activity;
        this.repetitions = repetitions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }
}
