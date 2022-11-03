package com.example.myapplication2;

import android.graphics.Bitmap;

public class SystemInfo {
    private static Bitmap profileImage=null;
    public static void setImageBitmap(Bitmap bitmap){
        profileImage=bitmap;
    }
    public static Bitmap getImageBitmap(){
        return profileImage;
    }
}
