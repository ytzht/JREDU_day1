package com.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.jredu_day1.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by zhang on 2015/11/17.p
 * 下载图片
 */

public class ImageLoaderUtil {
    private static DisplayImageOptions options =
            new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.banner_02)
            .showImageOnFail(R.drawable.banner_02)
            .showImageForEmptyUri(R.drawable.banner_02)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new FadeInBitmapDisplayer(200))
            .build();


    public ImageLoaderUtil() {}

    public static void display(String urlStr, ImageView imageView){
        ImageLoader.getInstance().displayImage(urlStr, imageView,options);
    }
}
