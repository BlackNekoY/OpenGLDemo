package com.example.slimxu.opengldemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.WindowManager;


/**
 * dp、sp 转换为 px 的工具类 
 */
@TargetApi(14)
public class DisplayUtil {

    public static final String TAG = "DisplayUtil";
    public static int sWindowWidth;
    public static int sWindowHeight;

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static int getWindowScreenWidth(Context context) {
        if (sWindowWidth > 0) {
            return sWindowWidth;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            sWindowWidth = size.x;
        } else {
            sWindowWidth = wm.getDefaultDisplay().getWidth();
        }

        return sWindowWidth;
    }

    public static int getWindowScreenHeight(Context context) {
        if (sWindowHeight > 0) {
            return sWindowHeight;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            sWindowHeight = size.y;
        } else {
            sWindowHeight = wm.getDefaultDisplay().getHeight();
        }

        return sWindowHeight;
    }

    public static int getStatusbarHeight(Context context){
        int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(id);
    }

    public static float getFitScaleValue(int width, int height,int maxWidth,int maxHeight) {
        if(height < maxHeight) {
            return 1;
        }else {
            return (float)maxHeight / (float)height;
        }

       /* float scale = 1F;
        if(width > maxWidth) {
            //若需要的宽度大于最大允许宽度
            scale = (float)maxWidth / (float)width;
        } else if(height > maxHeight) {
            //若需要的高度大于最大允许高度
            scale = (float)maxHeight / (float)height;
        }
        return scale;*/
    }

}