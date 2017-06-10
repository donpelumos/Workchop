package com.workchopapp.workchop;

/**
 * Created by BALE on 18/07/2016.
 */

public class ListVendorType {
    String menuName, menuCount;
    int menuImage;
    public ListVendorType(String menuName, int menuImage, String menuCount){
        this.menuImage = menuImage;
        this.menuName = menuName;
        this.menuCount = menuCount;
    }
}
