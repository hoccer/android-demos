package com.hoccer.demos.simplemessage;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.EnvironmentStatus;
import com.hoccer.api.android.AsyncLinccer;
import com.hoccer.api.android.LinccLocationManager;
import com.hoccer.api.android.AsyncLinccer.MessageType;

public class SimpleMessageDemo extends Activity {
    private LinccLocationManager mLocationManager;
    private AsyncLinccer         mLinccer;
    private Thread               mUpdateThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ClientConfig config = new ClientConfig("SimpleMessageDemo/Android",
                "e101e890ea97012d6b6f00163e001ab0", "JofbFD6w6xtNYdaDgp4KOXf/k/s=");

        mLinccer = new AsyncLinccer(config);
        mLocationManager = new LinccLocationManager(this, mLinccer);

        Button sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage(((EditText) findViewById(R.id.new_message)).getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        startLocationUpdates();
        startWatchingForMessages();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopLocationUpdates();
        super.onPause();
    }

    private void startWatchingForMessages() {
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
                                    Toast.makeText(SimpleMessageDemo.this, "received " + payload,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            };
        }.start();
    }

    private void sendMessage(String text) {
        try {
            mLinccer.asyncShare("one-to-many", new JSONObject("{message : '" + text + "'}"),
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what != MessageType.SHARED) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SimpleMessageDemo.this, "sending failed",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
        } catch (JSONException e) {
            Toast.makeText(SimpleMessageDemo.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                    } catch (Exception e) {
                        Toast.makeText(SimpleMessageDemo.this, e.getMessage(), Toast.LENGTH_LONG)
                                .show();
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
                statusView.setText("Location Quality is: "
                        + (envStatus == null ? 0 : envStatus.getQuality()));
            }
        });
    };

}
