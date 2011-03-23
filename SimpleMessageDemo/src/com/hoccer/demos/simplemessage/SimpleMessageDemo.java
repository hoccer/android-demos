package com.hoccer.demos.simplemessage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.EnvironmentStatus;
import com.hoccer.api.android.AsyncLinccer;
import com.hoccer.api.android.LinccLocationManager;

public class SimpleMessageDemo extends Activity {
    private LinccLocationManager mLocationManager;
    private AsyncLinccer         mLinccer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ClientConfig config = new ClientConfig("SimpleMessageDemo/Android",
                "e101e890ea97012d6b6f00163e001ab0", "JofbFD6w6xtNYdaDgp4KOXf/k/s=");

        mLinccer = new AsyncLinccer(config);
        mLocationManager = new LinccLocationManager(this, mLinccer);
    }

    @Override
    protected void onResume() {
        updateStatus();
        mLocationManager.activate();

        new Thread() {
            @Override
            public void run() {
                try {
                    mLocationManager.refreshLocation();
                    updateStatus();

                    Thread.sleep(10 * 1000);
                } catch (Exception e) {
                    Toast.makeText(SimpleMessageDemo.this, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        }.start();

        super.onResume();
    }

    @Override
    protected void onPause() {
        mLocationManager.deactivate();
        super.onPause();
    }

    private void updateStatus() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                EnvironmentStatus envStatus = mLinccer.getEnvironmentStatus();

                TextView statusView = (TextView) findViewById(R.id.status);
                statusView.setText("Location Quality is: "
                        + (envStatus == null ? 0 : envStatus.getQuality()));
            }
        });
    };

}
