package com.example.message1.ChatUsers;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.message1.Messeging.MessageActivity;
import com.example.message1.R;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter< ChatListAdapter.UserListViewHolder> {

    ArrayList<ChatObject> chatList;
    Context context;

    public ChatListAdapter(Context context, ArrayList<ChatObject> usersList)
    {
        this.chatList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new UserListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder userListViewHolder, final int i)
    {
        //here get name can be changed by declaring contactList final (in home activity):).
        final String name_str = chatList.get(i).getName();
        final String phone_str = chatList.get(i).getPhone();
        final String imageUrl = chatList.get(i).getImage();
        final String about_str = chatList.get(i).getAbout();

        //Both methods are working. (use whichever is faster)
        //ImageView pic1 = userListViewHolder.proPic;
        /*Picasso.get()
                .load(chatList.get(i).getImage())
                .resize(50, 50)
                .centerCrop()
                .into(pic1);  */

        Glide.with(this.context)
                .load(chatList.get(i).getImage())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userListViewHolder.getImage());

        //Picasso.get().load(imageUrl).into(userListViewHolder.pic2);

        userListViewHolder.name.setText( name_str );
        userListViewHolder.phone.setText(phone_str);

        if( chatList.get(i).getIs_message_came().equals("true"))
        {
            userListViewHolder.getImage2().setImageResource(R.drawable.message_received_icon);
        }

        userListViewHolder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("name", name_str);
                intent.putExtra("phone", phone_str);
                intent.putExtra("chatId", chatList.get(userListViewHolder.getAdapterPosition()).getChatId());
                intent.putExtra("image", imageUrl);
                intent.putExtra("about", about_str);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        //if(chatList != null)
            return chatList.size();
        //return 0;
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name, phone;
        private LinearLayout layout;
        private ImageView proPic, is_message_received;

        public  UserListViewHolder(View view)
        {
            super(view);
            name = (TextView) view.findViewById(R.id.userName_userList);
            phone = (TextView) view.findViewById(R.id.phone_usersList);
            proPic = (ImageView) view.findViewById(R.id.profile_pic_userList);
            layout = (LinearLayout) view.findViewById(R.id.item_user_layout);
            is_message_received = (ImageView) view.findViewById(R.id.is_message_received);
        }

        public ImageView getImage()
        {
            return this.proPic;
        }
        public ImageView getImage2()
        {
            return this.is_message_received;
        }
    }
}
