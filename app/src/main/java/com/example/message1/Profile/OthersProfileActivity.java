package com.example.message1.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.message1.R;

import org.w3c.dom.Text;

public class OthersProfileActivity extends AppCompatActivity {

    private TextView name, phone, about;
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        String nameString = getIntent().getExtras().get("name").toString();
        String imageUrl = getIntent().getExtras().get("image").toString();
        String phoneString = getIntent().getExtras().get("phone").toString();
        String aboutString = getIntent().getExtras().get("about").toString();

        name = (TextView) findViewById(R.id.name_othersProfile);
        phone = (TextView) findViewById(R.id.phone_othersProfile);
        pic = (ImageView) findViewById(R.id.image_othersProfile);
        about = (TextView) findViewById(R.id.about_othersProfile);

        Glide.with(this).load(imageUrl).centerCrop().into(pic);

        name.setText(nameString);
        phone.setText(phoneString);
        about.setText("     " + aboutString);
    }
}
