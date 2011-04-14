package com.hoccer.pictureslider;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.EnvironmentStatus;
import com.hoccer.api.android.AsyncLinccer;
import com.hoccer.api.android.LinccLocationManager;

public class PictureSlider extends Activity {
    protected static final String TAG               = "PictureSlider";
    private int                   currentImageIndex = 0;
    private AsyncLinccer          mLinccer;
    private LinccLocationManager  mLocationManager;
    private Thread                mUpdateThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ClientConfig config = new ClientConfig("PictureSlider/Android",
                "e101e890ea97012d6b6f00163e001ab0", "JofbFD6w6xtNYdaDgp4KOXf/k/s=");
        config.useProductionServers();
        mLinccer = new AsyncLinccer(config);
        mLocationManager = new LinccLocationManager(this, mLinccer);

        displayNextImage();

        Button sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendImageId(currentImageIndex);
            }
        });

        final ImageView image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayNextImage();

            }
        });

    }

    @Override
    protected void onResume() {
        startLocationUpdates();
        startWatchingForImages();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopLocationUpdates();
        super.onPause();
    }

    private void startWatchingForImages() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final JSONObject payload = mLinccer.receive("one-to-many", "waiting=true");
                        if (payload != null) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        displayImageId(payload.getInt("image_id"));
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.toString());
                                    }
                                }

                            });
                        }
                    } catch (Exception e) {

                    }

                }
            };
        }.start();

    }

    private void sendImageId(int id) {
        try {
            JSONObject json = new JSONObject();
            json.put("image_id", id);
            mLinccer.asyncShare("one-to-many", json, new Handler() {
                @Override
                public void handleMessage(final Message msg) {
                    super.handleMessage(msg);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(PictureSlider.this, "what? " + msg.what,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            Toast.makeText(PictureSlider.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startLocationUpdates() {
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
                                Toast.makeText(PictureSlider.this, e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        };
        mUpdateThread.start();
    }

    private void stopLocationUpdates() {
        mLocationManager.deactivate();
        mUpdateThread.interrupt();
    }

    private void updateStatus() {
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

    private void displayNextImage() {
        displayImageId(currentImageIndex + 1);
    }

    private void displayImageId(int id) {
        final ImageView image = (ImageView) findViewById(R.id.image);

        if (id > 2)
            id = 0;

        if (id == 0) {
            image.setImageResource(R.drawable.architecture);
        } else if (id == 1) {
            image.setImageResource(R.drawable.grouping);
        } else if (id == 2) {
            image.setImageResource(R.drawable.api);
        }

        currentImageIndex = id;
    }
}
