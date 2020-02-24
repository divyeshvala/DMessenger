package com.example.message1.Profile;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.message1.R;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTV, aboutTV;
    private static final int PICK_IMAGE = 1;
    private Button nameSave, aboutSave, nameEdit, aboutEdit;
    private EditText name, about;
    private ImageView proPic;
    private Uri imageUri;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageUri = null;

        name = (EditText) findViewById(R.id.userName_ET_profile);
        about = (EditText) findViewById(R.id.about_ET_profile);
        nameEdit = (Button) findViewById(R.id.userName_edit_btn);
        aboutEdit = (Button) findViewById(R.id.about_edit_btn);
        proPic = (ImageView) findViewById(R.id.pic_profile);
        nameTV = (TextView) findViewById(R.id.userName_TV_profile);
        aboutTV = (TextView) findViewById(R.id.about_TV_profile);
        nameSave = (Button) findViewById(R.id.userName_save_btn);
        aboutSave = (Button) findViewById(R.id.about_save_btn);

        name.setVisibility(View.INVISIBLE);
        nameSave.setVisibility(View.INVISIBLE);
        aboutSave.setVisibility(View.INVISIBLE);
        about.setVisibility(View.INVISIBLE);

        // Chasing dots progress Bar
        progressBar = (ProgressBar) findViewById(R.id.spin_kit_profile);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.VISIBLE);

        //Getting details of one user.
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String name_str = dataSnapshot.child("name").getValue(String.class);
                String phone_str = dataSnapshot.child("phone").getValue(String.class);
                String image_url = dataSnapshot.child("image").getValue(String.class);
                String about_str = dataSnapshot.child("about").getValue(String.class);

                nameTV.setText(name_str);
                name.setText(name_str);
                aboutTV.setText(about_str);

                Glide.with(ProfileActivity.this)
                        .load(image_url)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(proPic);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        proPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pick Image"), PICK_IMAGE);
            }
        });

        nameEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               name.setVisibility(View.VISIBLE);
               nameSave.setVisibility(View.VISIBLE);
               nameTV.setVisibility(View.INVISIBLE);
               nameEdit.setVisibility(View.INVISIBLE);
            }
        });

        aboutEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                about.setVisibility(View.VISIBLE);
                aboutSave.setVisibility(View.VISIBLE);
                aboutTV.setVisibility(View.INVISIBLE);
                aboutEdit.setVisibility(View.INVISIBLE);
            }
        });

        nameSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //it's fucking working...:) (changing the value of one field.
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("name").setValue(name.getText().toString());
                name.setVisibility(View.INVISIBLE);
                nameSave.setVisibility(View.INVISIBLE);
                nameTV.setVisibility(View.VISIBLE);
                nameEdit.setVisibility(View.VISIBLE);
                nameTV.setText(name.getText().toString());
            }
        });

        aboutSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("about").setValue(about.getText().toString());
                about.setVisibility(View.INVISIBLE);
                aboutSave.setVisibility(View.INVISIBLE);
                aboutTV.setVisibility(View.VISIBLE);
                aboutEdit.setVisibility(View.VISIBLE);
                aboutTV.setText(about.getText().toString());
            }
        });
    }

    private void uploadImage()
    {
        String uid = FirebaseAuth.getInstance().getUid();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(uid+currentDateandTime+".jpg");

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        final String link = uri.toString();

                        //now change image url in the database.
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("image").setValue(link);
                        progressBar.setVisibility(View.INVISIBLE);
                        //to enable user interaction
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            proPic.setImageURI(imageUri);
            progressBar.setVisibility(View.VISIBLE);

            //to disable user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            uploadImage();
        }
    }


}
