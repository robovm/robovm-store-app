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
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import org.robovm.store.R;

public class SlidingLayout extends LinearLayout {
    private static final int PRIMARY_VIEW_ID = R.id.productImage;
    private static final int SECONDARY_VIEW_ID = R.id.descriptionLayout;

    private View primaryView, secondaryView;
    private int initialMainViewDelta;

    public SlidingLayout(Context context) {
        super(context);
        initialize();
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        setOrientation(LinearLayout.VERTICAL);
    }

    public int getInitialMainViewDelta() {
        return initialMainViewDelta;
    }

    public void setInitialMainViewDelta(int initialMainViewDelta) {
        this.initialMainViewDelta = initialMainViewDelta;
    }

    public View getPrimaryView() {
        return primaryView == null ? primaryView = findViewById(PRIMARY_VIEW_ID) : primaryView;
    }

    public View getSecondaryView() {
        return secondaryView == null ? secondaryView = findViewById(SECONDARY_VIEW_ID) : secondaryView;
    }

    // Inverts children drawing order so that our main item (top) is drawn last
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return childCount - 1 - i;
    }

    @Override
    public float getTranslationY() {
        return primaryView.getTranslationY() / initialMainViewDelta;
    }

    @Override
    public void setTranslationY(float translationY) {
        primaryView.setTranslationY(translationY * initialMainViewDelta);
    }

    @Override
    public float getAlpha() {
        return secondaryView.getAlpha();
    }

    @Override
    public void setAlpha(float alpha) {
        secondaryView.setAlpha(alpha);
    }

    @Override
    public void setTranslationX(float translationX) {
        float power = (float) Math.pow(translationX, 5);
        super.setTranslationX(power * (getWidth() / 2));
        super.setTranslationY(-1 * power * (getHeight() / 2));
        super.setAlpha(1 - translationX);
        super.setScaleX(1 - .8f * translationX);
        super.setScaleY(1 - .8f * translationX);
    }
}
