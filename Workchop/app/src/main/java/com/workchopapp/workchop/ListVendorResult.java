package com.workchopapp.workchop;

/**
 * Created by BALE on 21/07/2016.
 */

public class ListVendorResult {
    String menuName;
    int menuImage;
    String menuLocation;
    String menuContactUsage;
    public ListVendorResult(String menuName, int menuImage, String menuLocation, String menuContactUsage){
        this.menuImage = menuImage;
        this.menuName = menuName;
        this.menuLocation = menuLocation;
        this.menuContactUsage = menuContactUsage;
    }
}
