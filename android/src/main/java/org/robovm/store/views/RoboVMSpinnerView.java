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

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import org.robovm.store.util.Colors;

public class RoboVMSpinnerView extends View {
    private AnimatorSet animation;
    private float rotation, scaleX, scaleY;

    private Path hexagon, cross;
    private Paint hexagonPaint, crossPaint;
    private Matrix transformationMatrix;

    public RoboVMSpinnerView(Context context) {
        super(context);
        initialize();
    }

    public RoboVMSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RoboVMSpinnerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        setScaleX(1);
        setScaleY(1);

        final int baseDuration = 1000;
        animation = new AnimatorSet();

        ObjectAnimator rotation = ObjectAnimator.ofFloat(this, "rotation", 0, 60);
        rotation.setDuration(baseDuration);
        rotation.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator scale = ObjectAnimator
                .ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("scaleX", 1, .9f),
                        PropertyValuesHolder.ofFloat("scaleY", 1, .9f));
        scale.setRepeatMode(ValueAnimator.REVERSE);
        scale.setDuration(baseDuration / 2);
        scale.setRepeatCount(ValueAnimator.INFINITE);

        animation.playTogether(rotation, scale);
        animation.start();
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        invalidate();
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    @Override
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHexagon(canvas);

        canvas.save();
        canvas.scale(.4f, .5f, getWidth() / 2, getHeight() / 2);
        drawCross(canvas);
        canvas.restore();
    }

    private void drawHexagon(Canvas canvas) {
        // The extra padding is to avoid edges being clipped
        int padding = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int halfHeight = (getHeight() - padding) / 2;
        int side = (getWidth() - padding) / 2;
        int foo = (int) Math.sqrt(side * side - halfHeight * halfHeight);

        Path path = hexagon == null ? hexagon = new Path() : hexagon;
        path.reset();
        path.moveTo(getWidth() / 2, padding / 2);
        path.rLineTo(-side / 2, 0);
        path.rLineTo(-foo, halfHeight);
        path.rLineTo(foo, halfHeight);
        path.rLineTo(side, 0);
        path.rLineTo(foo, -halfHeight);
        path.rLineTo(-foo, -halfHeight);
        path.close();

        Matrix m = transformationMatrix == null ? transformationMatrix = new Matrix() : transformationMatrix;
        m.reset();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        m.postRotate(rotation, centerX, centerY);
        m.postScale(scaleX, scaleY, centerX, centerY);
        path.transform(m);

        if (hexagonPaint == null) {
            hexagonPaint = new Paint();
            hexagonPaint.setColor(Color.rgb(0x22, 0x76, 0xB9));
            hexagonPaint.setAntiAlias(true);
            hexagonPaint.setPathEffect(new CornerPathEffect(30));
        }
        canvas.drawPath(path, hexagonPaint);
    }

    private void drawCross(Canvas canvas) {
        int smallSegment = getWidth() / 6;

        Path path = cross == null ? cross = new Path() : cross;
        path.reset();

        path.moveTo(0, 0);
        path.rLineTo(smallSegment, 0);
        path.lineTo(getWidth() / 2, getHeight() / 2);
        path.lineTo(getWidth() - smallSegment, 0);
        path.rLineTo(smallSegment, 0);
        path.lineTo(getWidth() / 2 + smallSegment, getHeight() / 2);
        path.lineTo(getWidth(), getHeight());
        path.rLineTo(-smallSegment, 0);
        path.lineTo(getWidth() / 2, getHeight() / 2);
        path.lineTo(smallSegment, getHeight());
        path.rLineTo(-smallSegment, 0);
        path.lineTo(getWidth() / 2 - smallSegment, getHeight() / 2);
        path.close();

        if (crossPaint == null) {
            crossPaint = new Paint();
            crossPaint.setAntiAlias(true);
            crossPaint.setColor(Colors.White);
        }

        canvas.drawPath(path, crossPaint);
    }
}
