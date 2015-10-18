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
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class ViewSwipeTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    private GestureDetector detector;
    private View targetView;
    private ViewConfiguration config;
    private int subviewID;

    private EventListener listener;

    public ViewSwipeTouchListener(Context context, int subviewID) {
        this.detector = new GestureDetector(context, this);
        this.config = ViewConfiguration.get(context);
        this.subviewID = subviewID;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (targetView == null) {
            targetView = subviewID == 0 ? v : v.findViewById(subviewID);
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (listener != null) {
                listener.onSwipeGestureEnd();
            }
            boolean dismiss = event.getAction() != MotionEvent.ACTION_CANCEL &&
                    targetView.getTranslationX() > targetView.getWidth() / 2;
            snapView(dismiss);
        }
        detector.onTouchEvent(event);
        return true;
    }

    public void resetSwipe() {
        if (targetView != null) {
            targetView.setAlpha(1);
            targetView.setTranslationX(0);
        }
    }

    private void snapView(boolean dismiss) {
        if (targetView == null) {
            return;
        }

        int targetAlpha = dismiss ? 0 : 1;
        int targetTranslation = dismiss ? targetView.getWidth() : 0;
        ObjectAnimator a = ObjectAnimator.ofPropertyValuesHolder(targetView,
                PropertyValuesHolder.ofFloat("alpha", targetView.getAlpha(), targetAlpha),
                PropertyValuesHolder.ofFloat("translationX", targetView.getTranslationX(), targetTranslation));

        if (dismiss) {
            a.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    animation.removeAllListeners();
                    if (listener != null) {
                        listener.onItemSwipped();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
        }
        a.start();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // We are only interested in an horizontal right-side fling
        if (velocityY > velocityX || velocityX < 0) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
        snapView(true);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (listener != null) {
            listener.onSwipeGestureBegin();
        }
        distanceX = -distanceX;
        if (Math.abs(distanceY) > Math.abs(distanceX) + config.getScaledTouchSlop() || (distanceX < 0
                && targetView.getTranslationX() <= 0)) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
        targetView.setTranslationX(Math.max(0, targetView.getTranslationX() + distanceX));
        targetView.setAlpha((targetView.getWidth() - targetView.getTranslationX()) / ((float) targetView.getWidth()));
        return true;
    }

    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }

    public interface EventListener {
        void onSwipeGestureBegin();

        void onSwipeGestureEnd();

        void onItemSwipped();
    }
}
