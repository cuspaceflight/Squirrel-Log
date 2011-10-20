package uk.ac.cam.cusf.squirrellog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.SystemClock;

public class BatteryLog extends Logger {

    private static final String LOG_NAME = "battery";

    public final BroadcastReceiver batteryListener;

    protected String logName() {
        return LOG_NAME;
    }

    protected BatteryLog() {
        super();

        logInterval = 0; // We want every battery update, regardless of how
                         // often they occur

        batteryListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
                            -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,
                            -1);
                    int voltage = intent.getIntExtra(
                            BatteryManager.EXTRA_VOLTAGE, -1);
                    int temperature = intent.getIntExtra(
                            BatteryManager.EXTRA_TEMPERATURE, -1);
                    int health = intent.getIntExtra(
                            BatteryManager.EXTRA_HEALTH,
                            BatteryManager.BATTERY_HEALTH_UNKNOWN);
                    long uptime = SystemClock.elapsedRealtime();

                    String info = level + "/" + scale;
                    info += "," + voltage;
                    info += "," + temperature;
                    info += ",";
                    if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
                        info += "GOOD";
                    } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                        info += "OVERHEAT";
                    } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
                        info += "DEAD";
                    } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                        info += "OVER_VOLTAGE";
                    } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                        info += "UNSPECIFIED_FAILURE";
                    } else {
                        // BatteryManager.BATTERY_HEALTH_COLD introduce in API
                        // level 11
                        info += "UNKNOWN";
                    }

                    info += "," + uptime;

                    append(info);
                }
            }
        };
    }

}
