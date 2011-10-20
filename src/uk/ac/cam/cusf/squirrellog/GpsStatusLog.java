package uk.ac.cam.cusf.squirrellog;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

public class GpsStatusLog extends Logger implements GpsStatus.Listener {

    private static final String LOG_NAME = "satellites";
    private LocationManager locMan;

    protected String logName() {
        return LOG_NAME;
    }

    protected GpsStatusLog(LocationManager locMan) {
        super();
        logInterval = 0;
        this.locMan = locMan;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
        case GpsStatus.GPS_EVENT_FIRST_FIX:

            break;
        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
            GpsStatus status = locMan.getGpsStatus(null);
            Iterable<GpsSatellite> satellites = status.getSatellites();
            String info;
            for (GpsSatellite gpsSatellite : satellites) {
                info = gpsSatellite.getPrn() + ",";
                info += gpsSatellite.getAzimuth() + ",";
                info += gpsSatellite.getElevation() + ",";
                info += gpsSatellite.getSnr() + ",";
                info += gpsSatellite.hasAlmanac() + ",";
                info += gpsSatellite.hasEphemeris() + ",";
                info += gpsSatellite.usedInFix();
                append(info);
            }
            break;
        case GpsStatus.GPS_EVENT_STARTED:

            break;
        case GpsStatus.GPS_EVENT_STOPPED:

            break;
        }
    }

}
