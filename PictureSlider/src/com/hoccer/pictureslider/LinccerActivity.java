package com.hoccer.pictureslider;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.EnvironmentStatus;
import com.hoccer.api.android.AsyncLinccer;
import com.hoccer.api.android.LinccLocationManager;

public class LinccerActivity extends Activity {

    protected static final String  TAG = "PictureSlider";
    protected AsyncLinccer         mLinccer;
    protected LinccLocationManager mLocationManager;
    private Thread                 mUpdateThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClientConfig config = new ClientConfig("PictureSlider/Android",
                "e101e890ea97012d6b6f00163e001ab0", "JofbFD6w6xtNYdaDgp4KOXf/k/s=");
        config.useProductionServers();
        mLinccer = new AsyncLinccer(config);
        mLocationManager = new LinccLocationManager(this, mLinccer);

    }

    protected void startLocationUpdates() {
        updateStatus();
        mLocationManager.activate();
        mUpdateThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        mLocationManager.refreshLocation();
                        updateStatus();
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        return;
                    } catch (final Exception e) {
                        Log.e(TAG, e.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LinccerActivity.this, e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        };
        mUpdateThread.start();
    }

    protected void stopLocationUpdates() {
        mLocationManager.deactivate();
        mUpdateThread.interrupt();
    }

    protected void updateStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EnvironmentStatus envStatus = mLinccer.getEnvironmentStatus();
                TextView statusView = (TextView) findViewById(R.id.status);
                statusView.setText("Location Quality is:"
                        + (envStatus == null ? 0 : envStatus.getQuality()));
            }
        });
    }

}
