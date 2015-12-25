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

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import org.robovm.store.R;

public class SwipableListItem extends FrameLayout {
    private static final int DARK_TONE = 0xd3;
    private static final int LIGHT_TONE = 0xdd;

    private static final int[] COLORS = new int[] {
            Color.rgb(DARK_TONE, DARK_TONE, DARK_TONE),
            Color.rgb(LIGHT_TONE, LIGHT_TONE, LIGHT_TONE),
            Color.rgb(LIGHT_TONE, LIGHT_TONE, LIGHT_TONE),
            Color.rgb(DARK_TONE, DARK_TONE, DARK_TONE)
    };
    private static final float[] POSITIONS = new float[] { 0, .15f, .85f, 1 };

    private View mainContent;
    private View secondaryContent;
    private ViewSwipeTouchListener listener;

    private Paint shadow;

    public SwipableListItem(Context context) {
        super(context);
        initialize();
    }

    public SwipableListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SwipableListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        shadow = new Paint();
        shadow.setAntiAlias(true);
        listener = new ViewSwipeTouchListener(getContext(), R.id.swipeContent);
        setOnTouchListener(listener);
        listener.addEventListener(new ViewSwipeTouchListener.EventListener() {
            @Override
            public void onSwipeGestureBegin() {}

            @Override
            public void onSwipeGestureEnd() {}

            @Override
            public void onItemSwipped() {
                listener.resetSwipe();
            }
        });
    }

    public ViewSwipeTouchListener getSwipeListener() {
        return listener;
    }

    public View getMainContent() {
        return mainContent == null ? mainContent = findViewById(R.id.swipeContent) : mainContent;
    }

    public View getSecondaryContent() {
        return null;
        // The id 'swipeAfter' doesn't exist anymore
        //return secondaryContent == null ? secondaryContent = findViewById(R.id.swipeAfter) : secondaryContent;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw interior shadow
        canvas.save();
        canvas.clipRect(0, 0, getWidth(), getHeight());
        canvas.drawPaint(shadow);
        canvas.restore();

        super.dispatchDraw(canvas);

        // Draw custom list separator
        canvas.save();
        canvas.clipRect(0, getHeight() - 2, getWidth(), getHeight());
        canvas.drawColor(Color.rgb(LIGHT_TONE, LIGHT_TONE, LIGHT_TONE));
        canvas.restore();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        shadow.setShader(new LinearGradient(0, 0, 0, bottom - top, COLORS, POSITIONS, Shader.TileMode.REPEAT));
    }
}
