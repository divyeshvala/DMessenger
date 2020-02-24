package com.example.message1.ChatUsers;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.example.message1.Utils.CountryToPhone;
import com.example.message1.ContactsUsers.FindUsersActivity;
import com.example.message1.R;
import com.example.message1.Settings.SettingsActivity;
import com.example.message1.ContactsUsers.UserObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity
{
    private Button settings;
    private ImageButton findUsers;
    private RecyclerView mChatListView;

    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    HashMap<String, String> newMessagingUsers ;
    ArrayList<ChatObject> mChatList;

    public static HashMap<String, String> contacts ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getPermissions();

        contacts = new HashMap<>();

        newMessagingUsers = new HashMap<>();
        mChatList = new ArrayList<ChatObject>();

        findUsers = (ImageButton) findViewById(R.id.findUsers_btn);
        settings = (Button) findViewById(R.id.settings_btn);

        getContactList();

        settings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        findUsers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, FindUsersActivity.class);
                startActivity(intent);
            }
        });

        initializeRecyclerView();

        getUserChatList();
    }

    private void getUserChatList()
    {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("chat");

        userDB.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    final String chatId = dataSnapshot.child("chatId").getValue(String.class);
                    String nameTemp = dataSnapshot.child("name").getValue(String.class);
                    final String phone = dataSnapshot.child("phone").getValue(String.class);
                    final String uid = dataSnapshot.child("uid").getValue(String.class);
                    final String is_message_came = dataSnapshot.child("is_message_came").getValue(String.class);

                    if( nameTemp != null && nameTemp.equals("xyz"))
                    {
                        if( contacts.containsKey(phone))
                        {
                            nameTemp = contacts.get(phone);
                        }
                    }
                    if(nameTemp == null)
                        nameTemp = "xyz";
                    final String name = nameTemp ;


                    DatabaseReference chatUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    chatUserDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            String image = dataSnapshot.child("image").getValue(String.class);
                            String about = dataSnapshot.child("about").getValue(String.class);

                            ChatObject mChat = new ChatObject(chatId, name, phone, image, about, is_message_came);

                            boolean exist = false ;
                            for(ChatObject obj : mChatList)
                            {
                                if(obj.getPhone().equals(phone))
                                {
                                    exist = true;
                                    mChatList.remove(obj);
                                    break;
                                }
                            }

                            mChatList.add(mChat);

                            mChatListLayoutManager.scrollToPosition(mChatList.size()-1);
                            mChatListAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                final String chatId = dataSnapshot.child("chatId").getValue(String.class);
                String nameTemp = dataSnapshot.child("name").getValue(String.class);
                final String phone = dataSnapshot.child("phone").getValue(String.class);
                final String uid = dataSnapshot.child("uid").getValue(String.class);
                final String is_message_came = dataSnapshot.child("is_message_came").getValue(String.class);

                if( nameTemp.equals("xyz"))
                {
                    if( contacts.containsKey(phone))
                    {
                        nameTemp = contacts.get(phone);
                    }
                }

                final String name = nameTemp ;

                DatabaseReference chatUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                chatUserDB.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String image = dataSnapshot.child("image").getValue(String.class);
                        String about = dataSnapshot.child("about").getValue(String.class);

                        ChatObject mChat = new ChatObject(chatId, name, phone, image, about, is_message_came);

                        boolean exist = false;
                        for(ChatObject obj : mChatList)
                        {
                            if(obj.getPhone().equals(phone))
                            {
                                exist = true;
                                mChatList.remove(obj);
                                break;
                            }
                        }

                        mChatList.add(mChat);
                        mChatListLayoutManager.scrollToPosition(mChatList.size()-1);
                        mChatListAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getContactList()
    {

        String ISOprefix = getCountryISO();

        Cursor phones = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null );

        while (phones.moveToNext())
        {
            String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
            String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            int hasPhoneNumber = Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if( hasPhoneNumber > 0)
            {
                Cursor cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] {id}, null);
                while(cursor2.moveToNext())
                {
                    String phone = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    phone = phone.replace(" ", "");
                    phone = phone.replace("-", "");
                    phone = phone.replace("(", "");
                    phone = phone.replace(")", "");


                    if( !String.valueOf(phone.charAt(0)).equals("+"))
                    {
                        phone = ISOprefix + phone;
                    }

                    UserObject mContact = new UserObject(name, phone, "NULL", "NULL", "NULL");
                    contacts.put(phone, name);
                }

                cursor2.close();
            }
        }
        phones.close();
    }

    private void initializeRecyclerView()
    {
        mChatListView = (RecyclerView) findViewById(R.id.chatList_recyclerView);
        mChatListView.setNestedScrollingEnabled(false);
        mChatListView.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(this);
        //for displaying bottom to top
        ((LinearLayoutManager) mChatListLayoutManager).setReverseLayout(true);
        mChatListView.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(HomeActivity.this, mChatList);
        mChatListView.setAdapter(mChatListAdapter);
    }

    private String getCountryISO()
    {
        String iso = null ;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        if( telephonyManager.getNetworkCountryIso() != null )
        {
            if( !telephonyManager.getNetworkCountryIso().toString().equals("") )
            {
                iso = telephonyManager.getNetworkCountryIso().toString();
            }
        }
        return CountryToPhone.getPhone( iso );
    }

    private void getPermissions()
    {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS
            }, 1 );
        }
    }
}
