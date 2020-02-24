package com.example.message1.Messeging;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.message1.ChatUsers.HomeActivity;
import com.example.message1.Profile.OthersProfileActivity;
import com.example.message1.R;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private RecyclerView messegesView;
    private RecyclerView.Adapter messegesAdapter;
    private RecyclerView.LayoutManager messegesLayoutManager;

    private TextView friendsName, friendsStatus;
    private ImageView friendsPic;

    private RelativeLayout sendLayout;
    private RelativeLayout header;

    private RelativeLayout imageLayout;
    private Button sendImage_btn;
    private ImageView selectedImage;

    private Button send_btn;
    private ImageButton selectImage;
    private Uri imageUri;
    private boolean isImage;

    ArrayList<MessageObject> messeges;

    private StorageReference mStorage;

    private ProgressBar progressBar;

    String chatID;
    String friends_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messeges = new ArrayList<MessageObject>();

        friendsName = (TextView) findViewById(R.id.friends_name_messages);
        friendsStatus = (TextView) findViewById(R.id.currentStatus_messages);
        friendsPic = (ImageView) findViewById(R.id.friends_Pic_messages);
        selectImage = (ImageButton) findViewById(R.id.selectImage_btn);

        progressBar = (ProgressBar) findViewById(R.id.spin_kit_sendImage);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.INVISIBLE);

        sendLayout = (RelativeLayout) findViewById(R.id.sendLayout);
        header = (RelativeLayout) findViewById(R.id.header);

        chatID = getIntent().getExtras().get("chatId").toString();
        friendsName.setText( getIntent().getExtras().get("name").toString());
        friendsStatus.setText(getIntent().getExtras().get("about").toString());
        String url = getIntent().getExtras().get("image").toString();
        friends_phone = getIntent().getExtras().get("phone").toString();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).
                child("chat").child(friends_phone).child("is_message_came").setValue("false");

        Glide.with(this)
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(friendsPic);

        header.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MessageActivity.this, OthersProfileActivity.class);
                intent.putExtra("name", getIntent().getExtras().get("name").toString());
                intent.putExtra("image", getIntent().getExtras().get("image").toString());
                intent.putExtra("phone", getIntent().getExtras().get("phone").toString());
                intent.putExtra("about", getIntent().getExtras().get("about").toString());
                startActivity(intent);
            }
        });

        initializeRecyclerView();

        send_btn = (Button) findViewById(R.id.sendButton_id);
        mStorage = FirebaseStorage.getInstance().getReference().child("chatImages");

        isImage = false;
        imageUri = null;

        imageLayout = (RelativeLayout) findViewById(R.id.imageDisplay_layout);
        sendImage_btn = (Button) findViewById(R.id.sendImage_btn);
        selectedImage = (ImageView) findViewById(R.id.displaySelectedImage_chat);
        imageLayout.setVisibility(View.INVISIBLE);

        selectImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessage();
            }
        });
        getMessages();
    }

    private void getMessages()
    {
        DatabaseReference mchatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        mchatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    String text="", creatorId="", image="", time="";
                    image = dataSnapshot.child("image").getValue(String.class);
                    text = dataSnapshot.child("text").getValue(String.class);
                    creatorId = dataSnapshot.child("creator").getValue(String.class);
                    time = dataSnapshot.child("time").getValue(String.class);

                    MessageObject messageObject = new MessageObject(dataSnapshot.getKey(), creatorId, text, image, time);

                    messeges.add(messageObject);
                    messegesLayoutManager.scrollToPosition(messeges.size()-1);
                    messegesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void sendMessage()
    {
        if( !isImage )
        {
            EditText messege_ET = (EditText) findViewById(R.id.messageToBeSend_id);
            String message_str = messege_ET.getText().toString();

            if( !message_str.isEmpty())
            {
                DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();

                Map<String, Object> messageData = new HashMap<>();
                messageData.put("image", "nullll");
                messageData.put("text", message_str);
                messageData.put("creator", FirebaseAuth.getInstance().getUid());

                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int min = Calendar.getInstance().get(Calendar.MINUTE);

                String current_time = (String.valueOf(hour))+":"+(String.valueOf(min));
                messageData.put("time", current_time);

                newMessageDB.updateChildren(messageData);

                updateIsMessageFieldOfFriend();
            }

            messege_ET.setText(null);
        }
        else if(imageUri != null )
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            StorageReference user_profile = mStorage.child(FirebaseAuth.getInstance().getUid()+currentDateandTime+".jpg");

            user_profile.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            final String photoStringLink = uri.toString();

                            DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();

                            Map<String, Object> messageData = new HashMap<>();
                            messageData.put("image", photoStringLink);
                            messageData.put("text", "nullll");
                            messageData.put("creator", FirebaseAuth.getInstance().getUid());

                            //Getting current time
                            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            int min = Calendar.getInstance().get(Calendar.MINUTE);
                            String current_time = (String.valueOf(hour))+":"+(String.valueOf(min));
                            messageData.put("time", current_time);

                            newMessageDB.updateChildren(messageData);

                            updateIsMessageFieldOfFriend();

                            progressBar.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
                }
            });

            imageUri = null;
            isImage = false;
        }
    }

    private void updateIsMessageFieldOfFriend()
    {
        //get friends uid
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("chat").child(friends_phone);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String friends_uid = dataSnapshot.child("uid").getValue(String.class);
                // Change is_message field of friend
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(friends_uid).child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                dbRef.child("is_message_came").setValue("true");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    private void initializeRecyclerView()
    {
        messegesView = (RecyclerView) findViewById(R.id.messeges_recyclerView);
        messegesView.setNestedScrollingEnabled(false);
        messegesView.setHasFixedSize(false);
        messegesLayoutManager = new LinearLayoutManager(this);
        messegesView.setLayoutManager(messegesLayoutManager);

        messegesAdapter = new MessageListAdapter(MessageActivity.this, messeges);
        messegesView.setAdapter(messegesAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE )
        {
            imageUri = data.getData();
            isImage = true;
            messegesView.setVisibility(View.INVISIBLE);
            sendLayout.setVisibility(View.INVISIBLE);
            header.setVisibility(View.INVISIBLE);
            imageLayout.setVisibility(View.VISIBLE);
            selectedImage.setImageURI(imageUri);
            sendImage_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    messegesView.setVisibility(View.VISIBLE);
                    sendLayout.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);

                    imageLayout.setVisibility(View.INVISIBLE);

                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    sendMessage();
                }
            });
        }
    }
}
