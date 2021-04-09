package pl.ppiwd.exerciseanalyst.services.metamotion;

import android.content.Context;
import android.util.Log;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;

import bolts.Continuation;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import pl.ppiwd.exerciseanalyst.repository.MeasurementsRepository;
import pl.ppiwd.exerciseanalyst.services.metamotion.helpers.ConnectionEventListener;
import pl.ppiwd.exerciseanalyst.services.metamotion.helpers.MetaWearConnectionEstablisher;
import pl.ppiwd.exerciseanalyst.services.metamotion.sensors.SensorsCtrl;
import pl.ppiwd.exerciseanalyst.services.metamotion.sensors.SensorsFactory;

public class MetaMotionCtrl implements ConnectionEventListener {
    private Context context;
    private String deviceMacAddress;
    private FrontendControl frontendControl;
    private MeasurementsRepository measurementsRepository;
    private String activity;
    private int numberOfRepetitions;
    private MetaWearBoard board;
    private SensorsCtrl sensorsCtrl;
    private Disposable sessionBeginDisposable;

    public MetaMotionCtrl(
            Context context,
            String deviceMacAddress,
            FrontendControl frontendControl,
            MeasurementsRepository measurementsRepository,
            String activity,
            int numberOfRepetitions) {
        this.context = context;
        this.deviceMacAddress = deviceMacAddress;
        this.frontendControl = frontendControl;
        this.measurementsRepository = measurementsRepository;
        this.activity = activity;
        this.numberOfRepetitions = numberOfRepetitions;
    }

    public void onDestroy() {
        if (sessionBeginDisposable != null && !sessionBeginDisposable.isDisposed())
            sessionBeginDisposable.dispose();
    }

    public void connectAsync(BtleService.LocalBinder serviceBinder) {
        MetaWearConnectionEstablisher connectionEstablisher = new MetaWearConnectionEstablisher(
                deviceMacAddress,
                this,
                context,
                serviceBinder
        );

        connectionEstablisher.connectAsync();
    }

    public void disconnectAsync() {
        if (sensorsCtrl != null) {
            sensorsCtrl.stopSensors();
        }

        if (board != null) {
            Log.i("MetaMotionCtrl", "disconnectAsync() Board tearDown");
            board.tearDown();
            board.disconnectAsync().continueWith((Continuation<Void, Void>) task -> {
                Log.i("MetaMotionCtrl", "disconnectAsync() Board disconnected");
                return null;
            });
        }
    }

    @Override
    public void onConnectionSuccessful(MetaWearBoard board) {
        this.board = board;
        this.board.tearDown();
        Log.i("MetaMotionCtrl", "onConnectionSuccessful() thread: "
                + Thread.currentThread());

        ConnectableObservable<Long> beginSessionObservable = measurementsRepository.beginSession(activity, numberOfRepetitions);
        sessionBeginDisposable = beginSessionObservable.subscribe(sessionId -> {
            Log.i("MetaMotionCtrl", "onConnectionSuccessful() success in observable." +
                    " Thread: " + Thread.currentThread());

            sensorsCtrl = new SensorsCtrl(
                    new SensorsFactory(this.board, frontendControl),
                    measurementsRepository
            );
            sensorsCtrl.startSensors();
            frontendControl.onMetaWearConnectionSuccess(sessionId);
        }, errorThrowable -> {
            Log.i("MetaMotionCtrl", "onConnectionSuccessful() error in observable: "
                    + errorThrowable + ", thread: " + Thread.currentThread());
        });
        Log.i("MetaMotionCtrl", "onConnectionSuccessful() calling observable connect()");
        beginSessionObservable.connect();
    }

    @Override
    public void onConnectionFailed() {
        frontendControl.onMetaWearConnectionError();
    }
}
