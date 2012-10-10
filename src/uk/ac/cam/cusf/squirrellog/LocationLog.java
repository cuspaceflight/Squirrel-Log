package uk.ac.cam.cusf.squirrellog;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class LocationLog extends Logger implements LocationListener {

    private static final String LOG_NAME = "gps";

    public final static String DESCENT_BROADCAST = "uk.ac.cam.cusf.intent.action.DESCENT";
    
    private double maxAltitude = 0;
    private double prevAltitude = 0;
    private int stableCount = 0;
    private boolean descentAlert = false;
    private boolean landedAlert = false;
    private Context context;
    
    public LocationLog(Context context) {
        super(LOG_NAME, 0);
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        
        //Log.i(TAG, "onLocationChanged");
        
        String info = location.getTime() + ",";
        info += location.getLatitude() + ",";
        info += location.getLongitude() + ",";
        info += location.getAltitude() + ",";
        info += location.getBearing() + ",";
        info += location.getSpeed() + ",";
        info += location.getAccuracy();

        append(info);
        
        double altitude = location.getAltitude();

        if (altitude > maxAltitude) {
            maxAltitude = altitude;
        } else if (altitude < 1000 && maxAltitude > 2000) {
            if (!descentAlert) {
                descentAlert = true;
                Intent intent = new Intent();
                intent.setAction(DESCENT_BROADCAST);
                intent.putExtra("descent", true);
                context.sendBroadcast(intent);  
                Log.i(TAG, "Descending broadcast sent!");
                intent = new Intent();
                intent.setAction("uk.ac.cam.cusf.intent.Tweet");
                intent.putExtra("message", "Descending below 1km, prepare for landing.");
                context.sendBroadcast(intent);
            } else if (!landedAlert && altitude != 0) {
                if (Math.abs(altitude - prevAltitude) < 2) {
                    stableCount++;
                    if (stableCount > 50) {
                        landedAlert = true;
                        Intent intent = new Intent();
                        intent.setAction(DESCENT_BROADCAST);
                        intent.putExtra("landed", true);
                        context.sendBroadcast(intent);
                        Log.i(TAG, "Landed broadcast sent!");
                        intent = new Intent();
                        intent.setAction("uk.ac.cam.cusf.intent.Tweet");
                        intent.putExtra("message", "The Squirrel has landed!");
                        context.sendBroadcast(intent);
                    }
                } else {
                    stableCount = 0;
                    prevAltitude = altitude;
                }
  
            }
        }
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
