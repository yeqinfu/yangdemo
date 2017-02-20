package com.example.yeqinfu.yangdemo;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by yeqinfu on 17-2-20.
 */
public class Utils {
    public static float getScreenWidth(Context context){
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return  wm.getDefaultDisplay().getWidth();
    }
    public static float getScreenHeight(Context context){
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return  wm.getDefaultDisplay().getHeight();
    }
}
