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

package org.robovm.store.views;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import org.robovm.store.util.BitmapHolder;

public class KenBurnsDrawable extends Drawable implements BitmapHolder {
    private int defaultColor;
    private int alpha;
    private Matrix matrix;
    private Paint paint;
    private boolean secondSlot;

    private Bitmap bmp1, bmp2;
    private BitmapShader shader1, shader2;

    public KenBurnsDrawable(int defaultColor) {
        this.defaultColor = defaultColor;
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
    }

    public Bitmap getFirstBitmap() {
        return bmp1;
    }

    public void setFirstBitmap(Bitmap firstBitmap) {
        this.bmp1 = firstBitmap;
        shader1 = null;
        invalidateSelf();
    }

    public Bitmap getSecondBitmap() {
        return bmp2;
    }

    public void setSecondBitmap(Bitmap secondBitmap) {
        this.bmp2 = secondBitmap;
        shader2 = null;
        invalidateSelf();
    }

    @Override
    public void setImageBitmap(Bitmap bmp) {
        if (secondSlot) {
            setSecondBitmap(bmp);
        } else {
            setFirstBitmap(bmp);
        }
        secondSlot = !secondSlot;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        if (alpha != 255) {
            paint.setAlpha(255);
            if (bmp2 != null) {
                if (shader1 == null) {
                    shader1 = new BitmapShader(bmp1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
                shader1.setLocalMatrix(matrix);
                paint.setShader(shader1);
                canvas.drawRect(bounds, paint);
            } else {
                canvas.drawColor(defaultColor);
            }
        }
        if (alpha != 0) {
            paint.setAlpha(alpha);
            if (bmp1 != null) {
                if (shader2 == null) {
                    shader2 = new BitmapShader(bmp2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
                shader2.setLocalMatrix(matrix);
                paint.setShader(shader2);
                canvas.drawRect(bounds, paint);
            } else {
                canvas.drawColor(defaultColor);
            }
        }
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
