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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coreanimation.CATransition;
import org.robovm.apple.coreanimation.CATransitionType;
import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSTimer;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAnimationOptions;
import org.robovm.store.util.Colors;

public class JBKenBurnsView extends UIView {
    private static final float ENLARGE_RATIO = 1.1f;
    private static final int IMAGE_BUFFER = 3;
    private final LinkedList<UIView> views = new LinkedList<>();
    private final Random random = new Random();
    private List<UIImage> images = new ArrayList<>();
    private int currentIndex;
    private NSTimer timer;

    private double imageDuration = 12;
    private boolean shouldLoop = true;
    private boolean isLandscape = true;

    private AnimationListener listener;

    public JBKenBurnsView(CGRect frame) {
        super(frame);
        setBackgroundColor(Colors.Clear);
        getLayer().setMasksToBounds(true);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        if (images.size() < currentIndex) {
            animate();
        }
    }

    public double getImageDuration() {
        return imageDuration;
    }

    public void setImageDuration(double imageDuration) {
        this.imageDuration = imageDuration;
    }

    public boolean shouldLoop() {
        return shouldLoop;
    }

    public void setLoop(boolean loop) {
        this.shouldLoop = loop;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public void setLandscape(boolean landscape) {
        this.isLandscape = landscape;
    }

    public void setImages(List<UIImage> images) {
        this.images = images;
    }

    public void animate() {
        if (timer != null) {
            timer.invalidate();
        }
        timer = new NSTimer(imageDuration, this::nextImage, null, true, true);
        timer.fire();
    }

    private void nextImage(NSTimer timer) {
        if (images.size() == 0 || currentIndex >= images.size() && !shouldLoop) {
            if (listener != null) {
                listener.finished();
            }
            return;
        }
        if (currentIndex >= images.size()) {
            currentIndex = 0;
        }

        UIImage image = images.get(currentIndex);
        currentIndex++;
        if (image == null || image.getSize().equals(CGSize.Zero())) {
            return;
        }

        CGRect bounds = getBounds();

        double resizeRatio = -1;
        double widthDiff = -1;
        double heightDiff = -1;
        double originX = -1;
        double originY = -1;
        double zoomInX = -1;
        double zoomInY = -1;
        double moveX = -1;
        double moveY = -1;
        double frameWidth = isLandscape ? bounds.getWidth() : bounds.getHeight();
        double frameHeight = isLandscape ? bounds.getHeight() : bounds.getWidth();

        // Wider than screen
        double imageWidth = image.getSize().getWidth() == 0 ? 100 : image.getSize().getWidth();
        double imageHeight = image.getSize().getHeight() == 0 ? 100 : image.getSize().getHeight();

        if (imageWidth > frameWidth) {
            widthDiff = imageWidth - frameWidth;

            // Higher than screen
            if (imageHeight > frameHeight) {
                heightDiff = imageHeight - frameHeight;

                if (widthDiff > heightDiff)
                    resizeRatio = frameHeight / imageHeight;
                else
                    resizeRatio = frameWidth / imageWidth;

                // No higher than screen [OK]
            } else {
                heightDiff = frameHeight - imageHeight;

                if (widthDiff > heightDiff)
                    resizeRatio = frameWidth / imageWidth;
                else
                    resizeRatio = bounds.getHeight() / imageHeight;
            }
            // No wider than screen
        } else {
            widthDiff = frameWidth - imageWidth;

            // Higher than screen [OK]
            if (imageHeight > frameHeight) {
                heightDiff = imageHeight - frameHeight;

                if (widthDiff > heightDiff)
                    resizeRatio = imageHeight / frameHeight;
                else
                    resizeRatio = frameWidth / imageWidth;

                // No higher than screen [OK]
            }
            else {
                heightDiff = frameHeight - imageHeight;

                if (widthDiff > heightDiff)
                    resizeRatio = frameWidth / imageWidth;
                else
                    resizeRatio = frameHeight / imageHeight;
            }
        }

        // Resize the image.
        double optimusWidth = (imageWidth * resizeRatio) * ENLARGE_RATIO;
        double optimusHeight = (imageHeight * resizeRatio) * ENLARGE_RATIO;

        UIImageView imageView = new UIImageView();
        imageView.setFrame(new CGRect(0, 0, optimusWidth, optimusHeight));
        imageView.setBackgroundColor(Colors.Clear);

        double maxMoveX = Math.min(optimusWidth - frameWidth, 50f);
        double maxMoveY = Math.min(optimusHeight - frameHeight, 50f) * 2f / 3;

        float rotation = random.nextInt(9) / 100;

        switch (random.nextInt(3)) {
        case 0:
            originX = 0;
            originY = 0;
            zoomInX = 1.25f;
            zoomInY = 1.25f;
            moveX = -maxMoveX;
            moveY = -maxMoveY;
            break;
        case 1:
            originX = 0;
            originY = 0;// Math.max(frameHeight - (optimusHeight),frameHeight *
                        // 1/3);
            zoomInX = 1.1f;
            zoomInY = 1.1f;
            moveX = -maxMoveX;
            moveY = maxMoveY;
            break;
        case 2:
            originX = frameWidth - optimusWidth;
            originY = 0;
            zoomInX = 1.3f;
            zoomInY = 1.3f;
            moveX = maxMoveX;
            moveY = -maxMoveY;
            break;
        default:
            originX = frameWidth - optimusWidth;
            originY = 0;// Math.max(frameHeight - optimusHeight,frameHeight *
                        // 1/3);
            zoomInX = 1.2f;
            zoomInY = 1.2f;
            moveX = maxMoveX;
            moveY = maxMoveY;
            break;
        }

        CALayer picLayer = new CALayer();
        picLayer.setContents(image.getCGImage());
        picLayer.setAnchorPoint(CGPoint.Zero());
        picLayer.setBounds(imageView.getBounds());
        picLayer.setPosition(new CGPoint(originX, originY));
        imageView.getLayer().addSublayer(picLayer);

        CATransition animation = new CATransition();
        animation.setDuration(1);
        animation.setType(CATransitionType.Fade);
        getLayer().addAnimation(animation, null);

        views.add(imageView);
        while (views.size() > IMAGE_BUFFER) {
            views.remove().removeFromSuperview();
        }
        addSubview(imageView);

        final double tx = moveX, ty = moveY;
        final double sx = zoomInX, sy = zoomInY;

        animate(imageDuration + 2, 0, UIViewAnimationOptions.CurveEaseIn, () -> {
            CGAffineTransform t = CGAffineTransform.createRotation(rotation);
            t.translate(tx, ty);
            t.scale(sx, sy);
            imageView.setTransform(t);
        }, null);

        if (listener != null) {
            listener.imageIndexChanged(currentIndex);
        }
    }

    public interface AnimationListener {
        void imageIndexChanged(int index);

        void finished();
    }
}
