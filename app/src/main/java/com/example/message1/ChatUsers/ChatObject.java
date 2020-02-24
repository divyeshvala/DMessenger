package com.example.message1.ChatUsers;

public class ChatObject
{
    private String chatId, name, phone, image, about, is_message_came;

    public ChatObject(){ }

    public ChatObject(String chatId, String name, String phone, String image, String about, String is_message_came)
    {
        this.chatId = chatId;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.about = about;
        this.is_message_came = is_message_came ;
    }

    public String getIs_message_came() {
        return is_message_came;
    }

    public void setIs_message_came(String is_message_came) {
        this.is_message_came = is_message_came;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
