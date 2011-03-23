package com.hoccer.demos.simplemessage;

import android.app.Activity;
import android.os.Bundle;

import com.hoccer.api.ClientConfig;
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
        mLocationManager.activate();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mLocationManager.deactivate();
        super.onPause();
    }
}
