package com.hoccer.pictureslider;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class PictureSlider extends Activity {
    protected static final String TAG               = "PictureSlider";
    private int                   currentImageIndex = 0;

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
            }
        });

    }

    private void displayNextImage() {
        final ImageView image = (ImageView) findViewById(R.id.image);

        if (currentImageIndex == 0) {
            image.setImageResource(R.drawable.architecture);
        } else if (currentImageIndex == 1) {
            image.setImageResource(R.drawable.grouping);
        } else if (currentImageIndex == 2) {
            image.setImageResource(R.drawable.api);
            currentImageIndex = 0;
        } else {
            currentImageIndex = 0;
        }

        currentImageIndex++;
    }
}
