/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mohammed Sazid-Al-Rashid
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mohammedsazid.android.launsz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HelperClass {

    /**
     * Save the given bitmap into the disk
     * @param context
     * @param fileName
     * @param icon
     */
    public static void saveIconToDisk(Context context, String fileName, Bitmap icon) {
        if (icon != null) {
            FileOutputStream resourceFile = null;
            try {
                resourceFile = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // save the file with 75% compression
                if (icon.compress(Bitmap.CompressFormat.PNG, 75, os)) {
                    byte[] buffer = os.toByteArray();
                    resourceFile.write(buffer);
                }
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (resourceFile != null) {
                    try {
                        resourceFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Saves a given drawable as Bitmap into the disk
     * @param context
     * @param fileName
     * @param iconDrawable
     */
    public static void saveIconToDisk(Context context, String fileName, Drawable iconDrawable) {
        if (iconDrawable != null) {
            saveIconToDisk(context, fileName, drawableToBitmap(iconDrawable));
        }
    }

    /**
     * Gets the icon from the disk and returns a bitmap of it
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getIconFromDisk(Context context, String fileName) {
        FileInputStream resourceFile = null;
        Bitmap bitmap = null;
        try {
            resourceFile = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            int bytesRead = 0;
            while (bytesRead >= 0) {
                bytes.write(buffer, 0, bytesRead);
                bytesRead = resourceFile.read(buffer, 0, buffer.length);
            }

            bitmap = BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.size());
            if (bitmap == null)
                Log.e(HelperClass.class.getSimpleName(), "Failed to decode the icon");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resourceFile != null) {
                try {
                    resourceFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    public static void dimBackground(Activity activity, SharedPreferences sharedPrefs) {
        float dim_percentage = sharedPrefs.getInt(
                activity.getString(R.string.bg_dim_amount_key),
                0
        );

        boolean dim_enabled = sharedPrefs.getBoolean(
                activity.getString(R.string.bg_dim_key),
                false
        );

        if (dim_enabled) {
            WindowManager.LayoutParams windowManager = activity.getWindow().getAttributes();
            windowManager.dimAmount = (dim_percentage / 100);

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    /**
     * Converts the given Drawable into a Bitmap.
     * If the Drawable is a BitmapDrawable, it returns that instead.
     *
     * @param drawable The Drawable to convert to Bitmap
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            if (((BitmapDrawable) drawable).getBitmap() != null) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(
                Math.max(drawable.getIntrinsicWidth(), 1),
                Math.max(drawable.getIntrinsicHeight(), 1),
                Bitmap.Config.ARGB_8888
        );
        // Construct the Canvas with the Bitmap to draw into
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(c);
        c.setBitmap(null);
        return bitmap;
    }

}
