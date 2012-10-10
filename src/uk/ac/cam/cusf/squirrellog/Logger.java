package uk.ac.cam.cusf.squirrellog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Log;

public abstract class Logger {

    public static final String TAG = "SquirrelLog";

    public static final String EXT = ".txt";

    public static final long RETRY_TIME = 30000;

    public static final long FLUSH_TIME = 60000; // Flush the BufferedWriter
                                                 // every minute

    private final String logName;
    
    private BufferedWriter writer;
    private long lastLog = 0;
    private boolean closed = false;

    private long openTime = 0;
    private boolean ERROR = false;

    // Minimum time (in milliseconds) between successive log updates
    private long logInterval;

    protected Logger(String logName, long logInterval) {
        this.logName = logName;
        this.logInterval = logInterval;
        openFile();
    }
    
    protected Logger(String logName) {
        this(logName, 500);
    }

    private boolean openFile() {
        File exportDir = new File(LogService.path, String.valueOf(LogService.instance));
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, logName + EXT);
        Log.i(TAG, file.getAbsolutePath());
        try {
            if (!file.exists())
                file.createNewFile();
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            Log.i(TAG, file.getName() + " opened for logging");
            ERROR = false;
        } catch (IOException e) {
            Log.e(TAG, "IOException in creating/opening file ("
                    + logName + ")", e);
            ERROR = true;
        }
        openTime = System.currentTimeMillis();
        return !ERROR;
    }

    protected boolean append(String log) {

        if (closed == true)
            return false;

        if (ERROR && System.currentTimeMillis() - openTime > RETRY_TIME) {
            Log.i(TAG, "Attempting to reopen " + logName);
            openFile();
        }

        if (ERROR)
            return false;

        long time = System.currentTimeMillis();
        long delay = time - lastLog;
        if (delay > logInterval) {
            try {
                writer.append(time + "," + log);
                writer.newLine();
                if (delay > FLUSH_TIME)
                    writer.flush();
                lastLog = time;
                return true;
            } catch (IOException e) {
                Log.e(TAG, "IOException in append() (" + logName + ")", e);
                ERROR = true;
                return false;
            }
        } else {
            return false;
        }
    }

    public void close() {
        closed = true;
        try {
            if (writer != null) {
                writer.close();
                Log.i(TAG, logName + EXT + " closed");
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in close() (" + logName + ")", e);
        }
    }

    public boolean isError() {
        return ERROR;
    }

}
