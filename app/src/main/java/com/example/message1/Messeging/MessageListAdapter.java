package com.example.message1.Messeging;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.message1.R;
import com.example.message1.ViewImage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1 ;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2 ;
    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;

    private Context mContext;
    private ArrayList<MessageObject> mMessageArrayList;

    public MessageListAdapter(Context context, ArrayList<MessageObject> messageArrayList) {
        mContext = context;
        mMessageArrayList = messageArrayList;
    }

    @Override
    public int getItemCount()
    {
        return mMessageArrayList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position)
    {
        MessageObject message = (MessageObject) mMessageArrayList.get(position);

        if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid()))
        {
            // If the current user is the sender of the message
            if(message.getText().equals("nullll"))
            {
                return VIEW_TYPE_IMAGE_SENT;
            }
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else
        {
            // If some other user sent the message
            if(message.getText().equals("nullll"))
            {
                return VIEW_TYPE_IMAGE_RECEIVED;
            }
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        else if( viewType == VIEW_TYPE_IMAGE_SENT )
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_sent, parent, false);
            return new SentImageHolder(view);
        }
        else if( viewType == VIEW_TYPE_IMAGE_RECEIVED )
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_received, parent, false);
            return new ReceivedImageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageObject message = (MessageObject) mMessageArrayList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_SENT:
                ((SentImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_RECEIVED:
                ((ReceivedImageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(MessageObject message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView imageSent;

        SentImageHolder(View itemView) {
            super(itemView);

            imageSent = (ImageView) itemView.findViewById(R.id.image_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(final MessageObject message) {

            Glide.with(mContext)
                    .load(message.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageSent);
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

            // *******************
            // This is testing  (if image viewing does not work delete this.)
            imageSent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Toast.makeText(mContext, "hi dude", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, ViewImage.class);
                    intent.putExtra("url", message.getImage());
                    mContext.startActivity(intent);
                }
            });
            // *******************
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(MessageObject message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

            // Insert the profile image from the URL into the ImageView.
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView imageReceived;

        ReceivedImageHolder(View itemView) {
            super(itemView);

            imageReceived = (ImageView) itemView.findViewById(R.id.image_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(final MessageObject message)
        {
            Glide.with(mContext)
                    .load(message.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageReceived);
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

            // *******************
            // This is testing  (if image viewing does not work delete this.)
            imageReceived.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Toast.makeText(mContext, "hi dude", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, ViewImage.class);
                    intent.putExtra("url", message.getImage());
                    mContext.startActivity(intent);
                }
            });
            // *******************
        }
    }
}