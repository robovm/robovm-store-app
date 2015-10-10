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
 * 
 */
package org.robovm.store.views;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIScrollViewDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.store.util.Action;

public class ImageScroller extends UIScrollView {
    private Action<Integer> imageChangeListener;
    private boolean isAnimating;
    private final List<UIImageView> imageViews = new ArrayList<>();
    private List<UIImage> images = new ArrayList<>();
    private int currentIndex;

    public ImageScroller() {
        setPagingEnabled(true);
        setShowsHorizontalScrollIndicator(false);

        setDelegate(new UIScrollViewDelegateAdapter() {
            @Override
            public void didScroll(UIScrollView scrollView) {
                scrolling();
            }

            @Override
            public void didEndScrollingAnimation(UIScrollView scrollView) {
                isAnimating = false;
                scrolling();
            }
        });
    }

    public List<UIImage> getImages() {
        return images;
    }

    public void setImages(List<UIImage> images) {
        this.images = images;
        updateImages();
    }

    private void updateImages() {
        for (UIImageView view : imageViews) {
            view.removeFromSuperview();
        }
        imageViews.clear();

        CGRect frame = getBounds();
        for (UIImage image : images) {
            UIImageView imageView = new UIImageView(image);
            imageView.setContentMode(UIViewContentMode.ScaleAspectFit);
            imageView.setFrame(frame);
            addSubview(imageView);
            imageViews.add(imageView);
            frame.setX(frame.getX() + frame.getWidth());
        }
        scrollToImage(currentIndex);
    }

    @Override
    public void setFrame(CGRect frame) {
        super.setFrame(frame);
        for (UIView view : getSubviews()) {
            view.setFrame(frame);
            frame.setX(frame.getX() + frame.getWidth());
        }
        frame.setWidth(frame.getX());
        setContentSize(frame.getSize());
        scrollToImage(currentIndex);
    }

    public void scrollToImage(int index) {
        if (index >= imageViews.size() || index == -1) {
            return;
        }
        isAnimating = true;
        UIImageView imageView = imageViews.get(index);
        scrollRectToVisible(imageView.getFrame(), true);
    }

    public void scrollToImage(UIImage image) {
        int index = images.indexOf(image);
        if (index == -1) {
            return;
        }
        scrollToImage(index);
        isAnimating = false;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        if (this.currentIndex == currentIndex) {
            return;
        }
        this.currentIndex = currentIndex;
        scrollToImage(currentIndex);

        if (imageChangeListener != null) {
            imageChangeListener.invoke(currentIndex);
        }
    }

    private void scrolling() {
        if (isAnimating) {
            return;
        }

        UIImageView page = null;
        for (UIImageView imageView : imageViews) {
            if (imageView.getFrame().contains(getContentOffset())) {
                page = imageView;
                break;
            }
        }

        int pageIndex = Math.max(imageViews.indexOf(page), 0);
        if (imageChangeListener != null && pageIndex != currentIndex) {
            imageChangeListener.invoke(pageIndex);
        }
        currentIndex = pageIndex;
    }

    public void setImageChangeListener(Action<Integer> listener) {
        this.imageChangeListener = listener;
    }
}
