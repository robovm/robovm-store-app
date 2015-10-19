/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.robovm.store.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Images {
    private static final ConcurrentMap<String, Bitmap> bmpCache = new ConcurrentHashMap<>();
    private static float SCREEN_WIDTH = 320;

    public static void setImageFromUrlAsync(ImageView imageView, String url) {
        fromUrl(url, imageView::setImageBitmap);
    }

    public static void setImageFromUrlAsync(BitmapHolder imageView, String url) {
        fromUrl(url, imageView::setImageBitmap);
    }

    public static void setImageFromUrlAsync(ImageView imageView, String url, Runnable completion) {
        fromUrl(url, (bitmap) -> {
            imageView.setImageBitmap(bitmap);
            completion.run();
        });
    }

    public static void setImageFromUrlAsync(BitmapHolder imageView, String url, Runnable completion) {
        fromUrl(url, (bitmap) -> {
            imageView.setImageBitmap(bitmap);
            completion.run();
        });
    }

    public static Bitmap fromUrl(String url) {
        Bitmap bmp = bmpCache.get(url);
        if (bmp == null) {
            File image = ImageCache.getInstance().downloadImage(url);
            bmp = saveBitmap(url, image);
        }
        return bmp;
    }

    public static void fromUrl(String url, Action<Bitmap> completion) {
        Bitmap bmp = bmpCache.get(url);
        if (bmp != null) {
            completion.invoke(bmp);
            return;
        }

        File image = ImageCache.getInstance().getImage(url);
        if (image != null) {
            completion.invoke(saveBitmap(url, image));
        } else {
            ImageCache.getInstance().downloadImage(url, (i) -> {
                completion.invoke(saveBitmap(url, i));
            });
        }
    }

    private static Bitmap saveBitmap(String url, File imagePath) {
        Bitmap bmp = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
        // We resize all bitmaps to screen width.
        bmp = resizeBitmap(bmp, SCREEN_WIDTH);

        bmpCache.put(url, bmp);
        return bmp;
    }

    private static Bitmap resizeBitmap(Bitmap bitmap, float maxImageSize) {
        float ratio = Math.min(maxImageSize / bitmap.getWidth(), maxImageSize / bitmap.getHeight());
        int width = Math.round(ratio * bitmap.getWidth());
        int height = Math.round(ratio * bitmap.getHeight());

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static void setScreenWidth(float screenWidth) {
        SCREEN_WIDTH = screenWidth;
    }
}
