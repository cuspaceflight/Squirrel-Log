package uk.ac.cam.cusf.squirrellog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LogService extends Service {

    public final static String TAG = "SquirrelLog";

    public static long instance = 0;

    private NotificationManager nManager;
    private SensorManager sensorMan;
    private LocationManager locMan;
    private TelephonyManager telMan;

    private AccelerometerLog accelerometerLog;
    private MagneticLog magneticLog;
    private OrientationLog orientationLog;
    private LocationLog locationLog;
    private GpsStatusLog gpsStatusListener;
    private NmeaLog gpsNmeaListener;
    private TelephonyLog telephonyLog;
    private BatteryLog batteryLog;
    private LogcatLog logcatLog;

    private boolean RUNNING = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sensorMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        telMan = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        showNotification();
    }

    @Override
    public void onDestroy() {

        RUNNING = false;
        unregisterListeners();

        closeLogs();
        nManager.cancel(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!RUNNING) {
            openLogs();
            registerListeners();
            RUNNING = true;
        }

        return START_STICKY;
    }

    private void openLogs() {
        instance = System.currentTimeMillis();
        accelerometerLog = new AccelerometerLog();
        magneticLog = new MagneticLog();
        orientationLog = new OrientationLog();
        locationLog = new LocationLog();
        gpsStatusListener = new GpsStatusLog(locMan);
        gpsNmeaListener = new NmeaLog();
        telephonyLog = new TelephonyLog();
        batteryLog = new BatteryLog();
        logcatLog = new LogcatLog();
    }

    private void closeLogs() {
        accelerometerLog.close();
        magneticLog.close();
        orientationLog.close();
        locationLog.close();
        gpsStatusListener.close();
        gpsNmeaListener.close();
        telephonyLog.close();
        batteryLog.close();
        logcatLog.close();
    }

    private void registerListeners() {
        sensorMan.registerListener(accelerometerLog, sensorMan
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(orientationLog, sensorMan
                .getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(magneticLog, sensorMan
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
                locationLog);
        locMan.addGpsStatusListener(gpsStatusListener);
        locMan.addNmeaListener(gpsNmeaListener);

        telMan.listen(telephonyLog.phoneStateListener,
                PhoneStateListener.LISTEN_CELL_LOCATION
                        | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                        | PhoneStateListener.LISTEN_SERVICE_STATE
                        | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        IntentFilter actionChanged = new IntentFilter();
        actionChanged.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLog.batteryListener, actionChanged);

        logcatLog.start();
    }

    private void unregisterListeners() {
        sensorMan.unregisterListener(accelerometerLog);
        sensorMan.unregisterListener(magneticLog);
        sensorMan.unregisterListener(orientationLog);

        locMan.removeGpsStatusListener(gpsStatusListener);
        locMan.removeNmeaListener(gpsNmeaListener);
        locMan.removeUpdates(locationLog);

        telMan.listen(telephonyLog.phoneStateListener,
                PhoneStateListener.LISTEN_NONE);

        unregisterReceiver(batteryLog.batteryListener);

        logcatLog.stop();
    }

    private void showNotification() {
        Notification notification = new Notification(R.drawable.icon,
                "SquirrelLog", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SquirrelLog.class), 0);
        notification.setLatestEventInfo(this, "SquirrelLog",
                "SquirrelLog is now running in the background!", contentIntent);
        notification.flags |= Notification.FLAG_NO_CLEAR
                | Notification.FLAG_ONGOING_EVENT;
        nManager.notify(1, notification);
    }

}