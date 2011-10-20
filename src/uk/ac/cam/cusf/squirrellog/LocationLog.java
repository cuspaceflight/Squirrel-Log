package uk.ac.cam.cusf.squirrellog;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationLog extends Logger implements LocationListener {

    private static final String LOG_NAME = "gps";

    protected String logName() {
        return LOG_NAME;
    }

    public LocationLog() {
        super();
        logInterval = 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        String info = location.getTime() + ",";
        info += location.getLatitude() + ",";
        info += location.getLongitude() + ",";
        info += location.getAltitude() + ",";
        info += location.getBearing() + ",";
        info += location.getSpeed() + ",";
        info += location.getAccuracy();

        append(info);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
