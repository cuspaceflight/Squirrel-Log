package uk.ac.cam.cusf.squirrellog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommandReceiver extends BroadcastReceiver {

    public final static String TAG = "SquirrelLog";
    
    public final static String SMS_RECEIVED = "uk.ac.cam.cusf.intent.SMS_RECEIVED";
    public final static String SMS_SEND = "uk.ac.cam.cusf.intent.SMS_SEND";

    public final static String LOG_ACTION = "uk.ac.cam.cusf.squirrellog.LOG_SERVICE";

    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (intent.getAction().equals(SMS_RECEIVED)) {
            
            Log.i(TAG, "SMS_RECEIVED");
            
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String command = intent.getStringExtra("command");
            
            Log.i(TAG, phoneNumber + ": " + command);
            
            String message = "SquirrelLog: ";
            
            if (command == null) {
                Log.e(TAG, "No command received");
                return;
            } else if (command.equals("status")) {
                
                message += LogStatus.isRunning() ? "Running, " : "Not Running, ";
                
                Date date = new Date(LogStatus.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                
                message += dateFormat.format(date);
                
            } else if (command.equals("start")) {
                
                if (LogStatus.isRunning()) {
                    message += "Already running!";
                } else {
                    Intent launch = new Intent(LOG_ACTION);
                    context.startService(launch);
                    message += "Not running, now launched";
                }
                
            } else if (command.equals("stop")) {
                
                if (LogStatus.isRunning()) {
                    Intent stop = new Intent(LOG_ACTION);
                    context.stopService(stop);
                    message += "Logging running, now stopped";
                } else {
                    message += "Logging not running";
                }

            }
            
            intent = new Intent();
            intent.setAction(SMS_SEND);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("message", message);
            
            context.sendBroadcast(intent);
            
        }
        
    }

}
