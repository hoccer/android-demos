package com.hoccer.pictureslider;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class PictureSlider extends LinccerActivity {
    private int currentImageIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        displayNextImage();

        final ImageView image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayNextImage();
                sendImageId(currentImageIndex);
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

    private void displayNextImage() {
        displayImageId(currentImageIndex + 1);
    }

    void displayImageId(int id) {
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
