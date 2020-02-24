package com.example.message1.ContactsUsers;

import java.io.Serializable;

public class UserObject implements Serializable
{
    String name, phone, uid, image, about;

    public UserObject()
    {
    }

    public UserObject(String name, String phone, String uid, String image, String about) {
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.image = image;
        this.about = about;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
