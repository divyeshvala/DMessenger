package com.example.message1;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ViewImage extends AppCompatActivity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        String url = getIntent().getExtras().get("url").toString();

        image = (ImageView) findViewById(R.id.viewImage_ImageView);

        Glide.with(this).load(url).into(image);

    }
}
