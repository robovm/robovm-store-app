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

public class CircleDrawable extends Drawable {
    private Bitmap bmp;
    private BitmapShader bmpShader;
    private Paint paint;
    private RectF oval;

    public CircleDrawable(Bitmap bmp) {
        this.bmp = bmp;
        bmpShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bmpShader);
        oval = new RectF();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(oval, paint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        oval.set(0, 0, bounds.width(), bounds.height());
    }

    @Override
    public int getIntrinsicWidth() {
        return bmp.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return bmp.getHeight();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }
}
