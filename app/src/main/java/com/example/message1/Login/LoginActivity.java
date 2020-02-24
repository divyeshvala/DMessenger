package com.example.message1.Login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.message1.ChatUsers.HomeActivity;
import com.example.message1.Utils.CountryToPhone;
import com.example.message1.R;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button submit, getCode, done, skip;
    private EditText phone, code, country;
    private TextView title, addPhoto, error;
    private static final int PICK_IMAGE = 1;
    private EditText name;
    private ImageView pic;
    private Uri imageUri;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;

    private String verificationId ;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private ProgressBar progressBar;

    FirebaseUser current_user ;

    DatabaseReference dataRef ;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        loggedInUser();

        getPermissions();

        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();

        // Chasing dots progress Bar
        progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.INVISIBLE);

        mStorage = FirebaseStorage.getInstance().getReference().child("images");
        imageUri = null;

        mAuth = FirebaseAuth.getInstance();

        phone = (EditText) findViewById(R.id.phoneNumber_ET);
        code = (EditText) findViewById(R.id.code_ET);
        submit = (Button) findViewById(R.id.submit_btn);
        getCode = (Button) findViewById(R.id.getCode_btn);
        error = (TextView) findViewById(R.id.error_TV);
        country = (EditText) findViewById(R.id.country_code_ET);
        skip = (Button) findViewById(R.id.skip_btn);

        error.setVisibility(View.INVISIBLE);

        title = (TextView) findViewById(R.id.getUserDetails_title_TV);
        name = (EditText) findViewById(R.id.getUserDetails_name_ET);
        pic = (ImageView) findViewById(R.id.getUserDetails_pic);
        addPhoto = (TextView) findViewById(R.id.getUserDetails_addPhoto_TV);
        done = (Button) findViewById(R.id.done_btn);

        addPhoto.setVisibility(View.INVISIBLE);
        pic.setVisibility(View.INVISIBLE);
        name.setVisibility(View.INVISIBLE);
        done.setVisibility(View.INVISIBLE);
        skip.setVisibility(View.INVISIBLE);

        country.setText(CountryToPhone.getPhone(countryCodeValue));  //getting country code value

        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uploadDetails();
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult( Intent.createChooser(intent,  "Pick image"), PICK_IMAGE);
            }
        });

        getCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String phone_number = country.getText().toString() + phone.getText().toString();
                if(TextUtils.isEmpty(phone_number))
                {
                    Toast.makeText(LoginActivity.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    //to disable user interaction while progress
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_number, 60, TimeUnit.SECONDS, LoginActivity.this, mCallbacks);
                }

            }

        });

        skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String verification_code = code.getText().toString();
                if(TextUtils.isEmpty(verification_code))
                {
                    Toast.makeText(LoginActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                }
                else if( verificationId == null )
                {
                    Toast.makeText(LoginActivity.this, "Please enter phone number first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verification_code);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                error.setVisibility(View.VISIBLE);
                error.setText("There was some error in verification");
                progressBar.setVisibility(View.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {

                //this method doesn't work everytime so it is better to remove it.
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //to enable user interaction
                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                verificationId = s;
                mResendToken = forceResendingToken;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            current_user = FirebaseAuth.getInstance().getCurrentUser();
                            dataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user.getUid());

                            dataRef.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if( !dataSnapshot.exists())
                                    {
                                        Map<String, Object> userDataMap = new HashMap<>();
                                        userDataMap.put("name", current_user.getPhoneNumber());
                                        userDataMap.put("phone", current_user.getPhoneNumber());
                                        userDataMap.put("image", "https://firebasestorage.googleapis.com/v0/b/message1-2c5ca.appspot.com/o/images%2Fprofile.png?alt=media&token=6da806ef-2dcd-4200-a35a-381636eb1a9d");
                                        userDataMap.put("about", "Cool");
                                        dataRef.updateChildren(userDataMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });
                            // Sign in success, update UI with the signed-in user's information
                            title.setText("Profile info");
                            submit.setVisibility(View.INVISIBLE);
                            code.setVisibility(View.INVISIBLE);
                            getCode.setVisibility(View.INVISIBLE);
                            error.setVisibility(View.INVISIBLE);
                            phone.setVisibility(View.INVISIBLE);
                            country.setVisibility(View.INVISIBLE);

                            progressBar.setVisibility(View.INVISIBLE);
                            //to enable user interaction
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            addPhoto.setVisibility(View.VISIBLE);
                            pic.setVisibility(View.VISIBLE);
                            name.setVisibility(View.VISIBLE);
                            done.setVisibility(View.VISIBLE);
                            skip.setVisibility(View.VISIBLE);
                        }
                        else {

                            progressBar.setVisibility(View.INVISIBLE);
                            //to enable user interaction
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                             if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                 error.setVisibility(View.VISIBLE);
                                 error.setText("Verification code is wrong");
                            }
                        }
                    }
                });
    }

    private void uploadDetails()
    {
        String current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference user_profile = mStorage.child(current_uid+".jpg");

        if(imageUri == null)
        {
            Toast.makeText(LoginActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        else if ( TextUtils.isEmpty(name.getText().toString()) )
        {
            Toast.makeText(LoginActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            user_profile.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            final String photoStringLink = uri.toString();

                            current_user = FirebaseAuth.getInstance().getCurrentUser();

                            dataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user.getUid());

                            dataRef.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if( dataSnapshot.exists())
                                    {
                                        Map<String, Object> userDataMap = new HashMap<>();
                                        userDataMap.put("name", name.getText().toString());
                                        userDataMap.put("phone", current_user.getPhoneNumber());
                                        userDataMap.put("image", photoStringLink);
                                        userDataMap.put("about", "Cool");
                                        dataRef.updateChildren(userDataMap);
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {
                                }
                            });
                            //Picasso.get().load(photoStringLink).into(pic2);
                        }
                    });
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void loggedInUser()
    {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        if(current_user != null )
        {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
            return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE )
        {
            imageUri = data.getData();
            pic.setImageURI(imageUri);
        }
    }

    private void getPermissions()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[] {
                    Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS
            }, 1 );
        }
    }
}
