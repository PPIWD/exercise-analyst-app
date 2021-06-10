package pl.ppiwd.exerciseanalyst.services.metamotion;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mbientlab.metawear.android.BtleService;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.activities.MenuActivity;
import pl.ppiwd.exerciseanalyst.common.BroadcastMsgs;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.common.ServiceActivityMsgSender;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementsDatabase;
import pl.ppiwd.exerciseanalyst.repository.MeasurementsRepository;
import pl.ppiwd.exerciseanalyst.services.metamotion.sensors.SensorType;
import pl.ppiwd.exerciseanalyst.services.utils.MainUiHandler;

public class MetaMotionService extends Service implements FrontendControl {
    public static final String CHANNEL_ID = "MetaMotionServiceChannel";
    private MainUiHandler mainUiHandler;
    private LocalBroadcastManager localBroadcastManager;
    private PowerManager.WakeLock wakeLock;
    private ServiceActivityMsgSender msgSender;
    private BroadcastReceiver metaMotionServiceAliveCheckReceiver;
    private MeasurementsRepository measurementsRepository;
    private BtleServiceConnection btleServiceConnection;
    private MetaMotionCtrl metaMotionCtrl;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MetaMotionService", "onCreate()");
        init();
    }

    private void init() {
        measurementsRepository = new MeasurementsRepository(
                MeasurementsDatabase.getInstance(getApplicationContext())
        );
        mainUiHandler = new MainUiHandler();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        msgSender = new ServiceActivityMsgSender(localBroadcastManager);
        metaMotionServiceAliveCheckReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                localBroadcastManager.sendBroadcastSync(new Intent(BroadcastMsgs.METAMOTION_SERVICE_ALIVE_RESP));
            }
        };
        localBroadcastManager.registerReceiver(metaMotionServiceAliveCheckReceiver,
                new IntentFilter(BroadcastMsgs.CHECK_METAMOTION_SERVICE_ALIVE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String deviceMacAddress = intent.getStringExtra(Constants.DEVICE_MAC_ADDRESS_KEY);
        String activity = intent.getStringExtra(Constants.ACTIVITY_NAME_KEY);
        int numberOfRepetitions = intent.getIntExtra(Constants.REPETITIONS_COUNT_KEY, 0);
        Log.i("MetaMotionService", "onStartCommand() device mac: " + deviceMacAddress);
        createNotificationChannel();
        startForeground(Constants.META_MOTION_SERVICE_NOTIFICATION_ID, buildNotification());
        acquirePartialWakeLock();
        bindMetaMotionBleService(deviceMacAddress, activity, numberOfRepetitions);
        return START_NOT_STICKY;
    }

    private void acquirePartialWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MetaMotionServiceLock:");
        }
        if (!wakeLock.isHeld())
            wakeLock.acquire();
    }

    private void bindMetaMotionBleService(String deviceMacAddress, String activity, int numberOfRepetitions) {
        metaMotionCtrl = new MetaMotionCtrl(
                this, deviceMacAddress, this, measurementsRepository, activity, numberOfRepetitions);
        btleServiceConnection = new BtleServiceConnection(metaMotionCtrl);
        bindService(
                new Intent(this, BtleService.class),
                btleServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    private void releasePartialWakeLock() {
        if (wakeLock != null && wakeLock.isHeld())
            wakeLock.release();
    }

    private void createNotificationChannel() {
        Log.i("MetaMotionService", "createNotificationChannel()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("MetaMotionService", "creating notif channel");
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "MetaMotionServiceChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, MenuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(Constants.NOTIFICATION_TITLE)
                        .setSmallIcon(R.drawable.baseline_fitness_center_white_18dp)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(createNotificationLayout())
                        .setContentIntent(pendingIntent)
                        .build();
        return notification;
    }

    private RemoteViews createNotificationLayout() {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);
        // if the notification is extended place appropriate initialising code here
        return notificationLayout;
    }

    @Override
    public void onDestroy() {
        Log.i("MetaMotionService", "onDestroy()");
        metaMotionCtrl.onDestroy();
        measurementsRepository.onDestroy();
        releasePartialWakeLock();
        localBroadcastManager.unregisterReceiver(metaMotionServiceAliveCheckReceiver);
        if (btleServiceConnection.isServiceBound()) {
            unbindService(btleServiceConnection);
        }
        metaMotionCtrl.disconnectAsync();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder() {
            public MetaMotionService getService() {
                return MetaMotionService.this;
            }
        };
    }

    @Override
    public void onMetaWearConnectionSuccess(long sessionId) {
        Log.i("MetaMotionService", "connected with MetaWear device, sessionId="
                + sessionId);
        mainUiHandler.postOnUI(() -> Toast.makeText(
                MetaMotionService.this,
                "Connected with MetaWear device. SessionId: " + sessionId,
                Toast.LENGTH_SHORT
        ).show());
    }

    @Override
    public void onMetaWearConnectionError() {
        Log.i("MetaMotionService", "Couldn't connect with MetaWear");
        mainUiHandler.postOnUI(() -> Toast.makeText(
                MetaMotionService.this,
                "Couldn't connect with MetaWear. Please try restarting this app." +
                        " This service will kill itself",
                Toast.LENGTH_LONG
        ).show());
        msgSender.send(BroadcastMsgs.METAMOTION_SERIVCE_TERMINATION);
        stopSelf();
    }

    @Override
    public void onSensorNotFound(SensorType sensorType) {
        Log.i("MetaMotionService", "Sensor not found: " + sensorType);
        mainUiHandler.postOnUI(() -> Toast.makeText(
                this,
                "Sensor not found: " + sensorType,
                Toast.LENGTH_SHORT
                ).show()
        );
    }
}