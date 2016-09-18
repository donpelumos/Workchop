package com.workchopapp.workchop;

/**
 * Created by BALE on 25/07/2016.
 */

public class ListChats {
    String personName;
    String dateTime;
    int personImage;
    int chatCount;
    public ListChats(String personName, int personImage, String dateTime, int chatCount){
        this.personImage = personImage;
        this.personName = personName;
        this.dateTime = dateTime;
        this.chatCount = chatCount;
    }
}
