package com.hoccer.pictureslider;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class PictureSlider extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageView image = (ImageView) findViewById(R.id.image);
        image.setImageResource(R.drawable.architecture);
    }
}
