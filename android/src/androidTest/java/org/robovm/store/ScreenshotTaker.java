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

package org.robovm.store;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScreenshotTaker {

    /**
     * Takes a screenshot of the given activity.
     * Saves the screenshot as png, it's saved as MM_DD_YY_HH-MM-SS_filename.png
     * All screenshots are saved on the external storage in the "screenshot" folder
     *
     * @param fileName the name of the file that is added after the timestamp
     * @param activity the activitiy to take the screenshot of
     */
    public static void takeScreenshotWithTimestamp(String fileName, Activity activity) {
        Date d = new Date();
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        String time = c.get(Calendar.MONTH) + "_" + c.get(Calendar.DAY_OF_MONTH)
                + "_" + c.get(Calendar.YEAR) + "_" + +c.get(Calendar.HOUR_OF_DAY)
                + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
        takeScreenshot(time + "_" + fileName, activity);
    }

    /**
     * Takes a screenshot of the given activity.
     * Saves the screenshot as png, it's saved as filename.png
     * All screenshots are saved on the external storage in the "screenshot" folder
     *
     * @param fileName the name of the screenshot file
     * @param activity the activitiy to take the screenshot of
     */
    public static void takeScreenshot(String fileName, Activity activity) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/screenshot");
        dir.mkdirs();
        File imageFile = new File(dir, fileName + ".png");

        View view = activity.getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        OutputStream os = null;

        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (Exception e) {
            }
        }
    }
}