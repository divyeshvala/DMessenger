package com.example.message1.ContactsUsers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.message1.ChatUsers.HomeActivity;
import com.example.message1.DatabaseHelper;
import com.example.message1.Messeging.MessageActivity;
import com.example.message1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter< UserListAdapter.UserListViewHolder > {

    ArrayList<UserObject> usersList;
    Context context;
    boolean key_already_exist = false;
    String key;

    public UserListAdapter(Context context, ArrayList<UserObject> usersList)
    {
        this.usersList = usersList;
        this.context = context;
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder
    {
        private TextView phone, name;
        private LinearLayout layout;
        private ImageView pic;

        public  UserListViewHolder(View view)
        {
            super(view);

            name = (TextView) view.findViewById(R.id.userName_userList);
            phone = (TextView) view.findViewById(R.id.phone_usersList);
            layout = (LinearLayout) view.findViewById(R.id.item_user_layout);
            pic = (ImageView) view.findViewById(R.id.profile_pic_userList);
        }

        public ImageView getImage()
        {
            return this.pic;
        }
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new UserListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder userListViewHolder, final int i)
    {
        try
        {
            Glide.with(this.context)
                    .load(usersList.get(i).getImage())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userListViewHolder.getImage());
        }
        catch (Exception e)
        {
            Toast.makeText(this.context, "Couldn't load images", Toast.LENGTH_SHORT).show();
        }

        userListViewHolder.name.setText(usersList.get(i).getName());
        userListViewHolder.phone.setText(usersList.get(i).getPhone());

        userListViewHolder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

                key_already_exist = false;
                //here don't add directly use value event listener...

                final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("chat").child( usersList.get(i).getPhone() );

                dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!(dataSnapshot.exists()))
                        {
                            Map<String, Object> data1 = new HashMap<>();
                            data1.put("name", usersList.get(i).getName());
                            data1.put("phone", usersList.get(i).getPhone());
                            data1.put("chatId", key);
                            data1.put("uid", usersList.get(i).getUid());
                            data1.put("is_message_came", "false");
                            dataRef.updateChildren(data1);

                            final DatabaseReference dataRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(usersList.get(i).getUid()).child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                            dataRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    final Map<String, Object> data2 = new HashMap<>();
                                    String my_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                                    data2.put("name", "xyz");
                                    data2.put("phone", my_phone);
                                    data2.put("chatId", key);
                                    data2.put("uid", FirebaseAuth.getInstance().getUid());
                                    data2.put("is_message_came", "false");
                                    dataRef2.updateChildren(data2);

                                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                                    intent.putExtra("name", usersList.get(i).getName());
                                    intent.putExtra("phone", usersList.get(i).getPhone());
                                    intent.putExtra("chatId", key);
                                    intent.putExtra("image", usersList.get(i).getImage());
                                    intent.putExtra("about", usersList.get(i).getAbout());
                                    v.getContext().startActivity(intent);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                        else
                        {
                            String key_original = dataSnapshot.child("chatId").getValue(String.class);
                            Toast.makeText(v.getContext(), "already exist...", Toast.LENGTH_SHORT).show();
                            //value of key wasn't changing outside so change activity from here...(avoid duplication of key if
                            // key already exist.

                            Intent intent = new Intent(v.getContext(), MessageActivity.class);
                            intent.putExtra("name", usersList.get(i).getName());
                            intent.putExtra("phone", usersList.get(i).getPhone());
                            intent.putExtra("chatId", key_original);
                            intent.putExtra("image", usersList.get(i).getImage());
                            intent.putExtra("about", usersList.get(i).getAbout());

                            v.getContext().startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return usersList.size();
    }
}
