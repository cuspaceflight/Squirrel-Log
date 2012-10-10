package uk.ac.cam.cusf.squirrellog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Handler;
import android.util.Log;

public class LogcatLog {

    public static final String TAG = "SquirrelLog";

    private static final String LOG_NAME = "logcat";
    private int LOG_COUNT = 0;

    private Thread thread;
    private Process process;

    private BufferedOutputStream out;

    private boolean ERROR = false;

    private boolean threadStop = false;

    private Handler handler;
    private Runnable startRunnable;

    protected String logName() {
        return LOG_NAME + LOG_COUNT;
    }

    public LogcatLog() {

        handler = new Handler();
        startRunnable = new Runnable() {
            @Override
            public void run() {
                if (out != null)
                    close();
                if (openFile()) {
                    thread = logcatThread();
                    thread.start();
                } else {
                    handler.postDelayed(this, Logger.RETRY_TIME);
                }
            }
        };

    }

    private boolean openFile() {
        LOG_COUNT++; // So as not to append to previous log file (some logcat
                     // entries would be duplicated)
        File exportDir = new File(LogService.path, String.valueOf(LogService.instance));
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, logName() + Logger.EXT);
        try {
            if (!file.exists())
                file.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(file));
            Log.i(TAG, file.getName() + " opened for logging");
            ERROR = false;
        } catch (IOException e) {
            Log.e(TAG, "IOException in creating/opening file (" + logName()
                    + ")", e);
            ERROR = true;
        }

        return !ERROR;
    }

    public void start() {
        handler.post(startRunnable);
    }

    public void stop() {
        handler.removeCallbacks(startRunnable);
        threadStop = true;
    }

    public void close() {
        try {
            if (out != null) {
                out.close();
                out = null;
                Log.i(TAG, logName() + Logger.EXT + " closed");
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in close() (" + logName() + ")", e);
        }
    }

    private Thread logcatThread() {
        return new Thread() {
            @Override
            public void run() {
                try {
                    
                    if (out != null) {
                        String[] cmd = new String[] { "logcat", "-v", "time",
                                "*:I" };
                        process = Runtime.getRuntime().exec(cmd);

                        InputStream inputStream = process.getInputStream();

                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while (!threadStop
                                && (read = inputStream.read(bytes)) != -1) {
                            if (!threadStop)
                                out.write(bytes, 0, read);
                        }

                        inputStream.close();
                        out.flush();

                        process.destroy();

                        if (!threadStop)
                            restart();

                    }

                } catch (IOException e) {
                    Log.e(TAG, "IOException in LogcatLog", e);
                    if (!threadStop)
                        restart();
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected Exception", e);
                    if (!threadStop)
                        restart();
                }
            }
        };
    }

    private void restart() {
        Log.i(TAG, "Restarting Logcat thread");
        handler.postDelayed(startRunnable, Logger.RETRY_TIME);
    }

}
