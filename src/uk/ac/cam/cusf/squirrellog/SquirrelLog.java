package uk.ac.cam.cusf.squirrellog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SquirrelLog extends Activity implements OnClickListener {

    private static final String TAG = "SquirrelLog";

    private Button startButton;
    private Button stopButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.startButton = (Button) this.findViewById(R.id.startbutton);
        this.startButton.setOnClickListener(this);

        this.stopButton = (Button) this.findViewById(R.id.stopbutton);
        this.stopButton.setOnClickListener(this);

    }

    public void onClick(View src) {
        switch (src.getId()) {
        case R.id.startbutton:
            startService(new Intent(this, LogService.class));
            break;
        case R.id.stopbutton:
            stopService(new Intent(this, LogService.class));
            break;
        }
    }

    private void copyFiles(File file, File dest) {
        copyFiles(file, file.getAbsolutePath(), dest.getAbsolutePath());
    }
    
    private void copyFiles(File file, String base, String dest) {
        if (!file.isDirectory()) {
            try {
                String path = file.getAbsolutePath().replace(base, dest);
                Log.i(TAG, "New " + path);
                File copy = new File(path);
                copy.createNewFile();
                FileChannel src = new FileInputStream(file).getChannel();
                FileChannel dst = new FileOutputStream(path).getChannel();
                
                dst.transferFrom(src, 0, src.size());
                
                src.close();
                dst.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException in copyFiles()", e);
            }
        } else {
            if (!file.getAbsolutePath().equals(base)) {
                String path = file.getAbsolutePath().replace(base, dest);
                Log.i(TAG, "Dir " + path);
                File dir = new File(path);
                dir.mkdirs();
            }
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                copyFiles(child, base, dest);
            }
        }
    }
    
    private void deleteFiles(File file, File base) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteFiles(child, base);
            }
        }
        
        if (!file.equals(base)) file.delete();
    }

    
    public void copyDataToSD() {
        
        File dir = getFilesDir();
        File sd = new File(Environment.getExternalStorageDirectory(), "SquirrelLog");
        copyFiles(dir, sd);
        
    }
    
    public void deleteData() {
        File dir = getFilesDir();
        deleteFiles(dir, dir);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.save:
            copyDataToSD();
            return true;
        case R.id.delete:
            deleteData();
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
}