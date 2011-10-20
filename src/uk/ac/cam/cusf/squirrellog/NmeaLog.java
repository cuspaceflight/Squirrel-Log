package uk.ac.cam.cusf.squirrellog;

import android.location.GpsStatus.NmeaListener;

public class NmeaLog extends Logger implements NmeaListener {

    private static final String LOG_NAME = "nmea";

    protected String logName() {
        return LOG_NAME;
    }

    public NmeaLog() {
        super();
        logInterval = 0;
    }

    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
        append(timestamp + "," + nmea);
    }

}
