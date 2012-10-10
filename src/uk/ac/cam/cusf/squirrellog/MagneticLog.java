package uk.ac.cam.cusf.squirrellog;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MagneticLog extends Logger implements SensorEventListener {

    private static final String LOG_NAME = "magnetic";

    public MagneticLog() {
        super(LOG_NAME);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        append(values[0] + "," + values[1] + "," + values[2]);
    }

}
