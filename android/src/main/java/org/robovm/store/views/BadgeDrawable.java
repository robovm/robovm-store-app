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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import org.robovm.store.util.Colors;

public class BadgeDrawable extends Drawable {
    private Drawable child;
    private Paint badgePaint, textPaint;
    private RectF badgeBounds = new RectF();
    private Rect txtBounds = new Rect();
    private int count = 0;
    private int alpha = 0xFF;

    private ValueAnimator alphaAnimator;

    public BadgeDrawable(Drawable child) {
        this.child = child;

        badgePaint = new Paint();
        badgePaint.setAntiAlias(true);
        badgePaint.setColor(Colors.White);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Colors.Green);
        textPaint.setTextSize(16);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        invalidateSelf();
    }

    public void setCountAnimated(final int count) {
        if (alphaAnimator != null) {
            alphaAnimator.cancel();
            alphaAnimator = null;
        }
        final int duration = 300;

        alphaAnimator = ObjectAnimator.ofInt(this, "alpha", 0xFF, 0);
        alphaAnimator.setDuration(duration);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setRepeatCount(1);
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
                animation.removeAllListeners();
                setCount(count);
            }
        });
        alphaAnimator.start();
    }

    @Override
    public void draw(Canvas canvas) {
        child.draw(canvas);
        if (count <= 0) {
            return;
        }
        badgePaint.setAlpha(alpha);
        textPaint.setAlpha(alpha);
        badgeBounds.set(0, 0, getBounds().width() / 2, getBounds().height() / 2);
        canvas.drawRoundRect(badgeBounds, 8, 8, badgePaint);
        textPaint.setTextSize((8 * badgeBounds.height()) / 10);
        String text = String.valueOf(count);
        textPaint.getTextBounds(text, 0, text.length(), txtBounds);
        canvas.drawText(text, badgeBounds.centerX(),
                badgeBounds.bottom - (badgeBounds.height() - txtBounds.height()) / 2 - 1, textPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        child.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    @Override
    public int getIntrinsicWidth() {
        return child.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return child.getIntrinsicHeight();
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        child.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return child.getOpacity();
    }
}
