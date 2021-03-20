package pl.ppiwd.exerciseanalyst.repository;

import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.model.Measurement;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementInsertCommand;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementsDatabase;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.persistence.dao.SessionDao;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionEntity;


public class MeasurementsRepository {

    private MeasurementsDao measurementsDao;
    private SessionDao sessionDao;
    private Disposable disposableIdOfInsertedSession;
    private Disposable measuremntsInsertionDisposable;
    private AtomicLong currentSessionId;
    private MeasurementsBuffer buffer;

    public MeasurementsRepository(MeasurementsDatabase measurementsDb) {
        this.measurementsDao = measurementsDb.measurementsDao();
        this.sessionDao = measurementsDb.sessionDao();
        this.currentSessionId = new AtomicLong(0);
        this.buffer = new MeasurementsBuffer(Constants.MEASUREMENTS_BUFFER_LIMIT);
    }

    public void onDestroy() {
        if (disposableIdOfInsertedSession != null && !disposableIdOfInsertedSession.isDisposed())
            disposableIdOfInsertedSession.dispose();

        if (measuremntsInsertionDisposable != null && !measuremntsInsertionDisposable.isDisposed())
            measuremntsInsertionDisposable.dispose();
    }

    public ConnectableObservable<Long> beginSession() {
        Log.i("MeasurementsRepository", "beginSession()");

        ConnectableObservable<Long> idOfInsertedSessionObservable =
                sessionDao.insertSession(new SessionEntity("NONE", 0))
                        .subscribeOn(Schedulers.io())
                        .toObservable().publish();

        disposableIdOfInsertedSession = idOfInsertedSessionObservable.subscribe(idOfInserted -> {
            Log.i("MeasurementsRepository()", "beginSession() success. id=" + idOfInserted
                    + ", Thread: " + Thread.currentThread());
            currentSessionId.set(idOfInserted);
        }, errorThrowable -> {
            Log.i("MeasurementsRepository", "beginSession() error: " + errorThrowable
                    + ", Thread: " + Thread.currentThread());
        });
        return idOfInsertedSessionObservable;
    }

    public void storeMeasurement(Measurement measurement) {
        Log.d("MeasurementsRepository", "storeMeasurement() buffer size before add: "
                + buffer.size());

        buffer.addMeasurement(measurement);
        if (buffer.isFull())
            storeMeasurementsFromBuffer();
    }

    private void storeMeasurementsFromBuffer() {
        Log.i("MeasurementsRepository", "storeMeasurementsFromBuffer()");
        MeasurementInsertCommand command = new MeasurementInsertCommand(
                measurementsDao,
                currentSessionId.get()
        );
        buffer.getMeasurements().forEach(meas -> meas.configureInserter(command));
        buffer.resetBuffer();
        measuremntsInsertionDisposable = command.execute().subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    Log.i("MeasurementsRepository", "storeMeasurement() success");
                }, errorThrowable -> {
                    Log.i("MeasurementsRepository", "storeMeasurement() error: "
                            + errorThrowable);
                });
    }
}
