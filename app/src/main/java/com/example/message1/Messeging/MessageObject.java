package com.example.message1.Messeging;

public class MessageObject
{
    String messageId, senderId, text, image, time;

    public MessageObject() {
    }

    public MessageObject(String messageId, String senderId, String text, String image, String time)
    {
        this.messageId = messageId;
        this.senderId = senderId;
        this.image = image;
        this.text = text;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
