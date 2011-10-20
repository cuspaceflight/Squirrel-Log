package uk.ac.cam.cusf.squirrellog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SquirrelLog extends Activity implements OnClickListener {

    // private static final String TAG = "SquirrelLog";

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

}