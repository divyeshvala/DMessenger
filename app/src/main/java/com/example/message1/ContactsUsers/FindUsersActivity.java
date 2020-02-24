package com.example.message1.ContactsUsers;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.message1.ChatUsers.HomeActivity;
import com.example.message1.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FindUsersActivity extends AppCompatActivity
{
    private RecyclerView mUsersListView;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;
    public ArrayList<UserObject> usersList;
    HashMap<String, String> contacts;

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_find_users);

        usersList = new ArrayList<UserObject>();

        contacts = HomeActivity.contacts ;

        initializeRecyclerView();

        getUsersList();
    }

    private void getUsersList()
    {
        final String current_Uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users");

        userDB.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {
                        String phone = childSnapshot.child("phone").getValue(String.class);
                        String image = childSnapshot.child("image").getValue(String.class);
                        String about = childSnapshot.child("about").getValue(String.class);
                        if( !(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals(phone)))
                        {
                            if(contacts.containsKey(phone))
                            {
                                    UserObject userObject = new UserObject( contacts.get(phone), phone, childSnapshot.getKey(), image, about);
                                    usersList.add(userObject);
                                    mUserListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    private void initializeRecyclerView()
    {
        mUsersListView = (RecyclerView) findViewById(R.id.usersList_recyclerview);
        mUsersListView.setNestedScrollingEnabled(false);
        mUsersListView.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(this);
        mUsersListView.setLayoutManager(mUserListLayoutManager);

        mUserListAdapter = new UserListAdapter(FindUsersActivity.this, usersList);
        mUsersListView.setAdapter(mUserListAdapter);
    }

}
