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

import org.robovm.apple.coreanimation.CAAnimationCalculationMode;
import org.robovm.apple.coreanimation.CAFillMode;
import org.robovm.apple.coreanimation.CAKeyframeAnimation;
import org.robovm.apple.coreanimation.CAMediaTimingFunction;
import org.robovm.apple.coreanimation.CAMediaTimingFunctionName;
import org.robovm.apple.coreanimation.CATransform3D;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSValue;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageRenderingMode;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.store.util.Colors;

public class BasketButton extends UIControl {
    private static final int PADDING = 10;

    private final BadgeView badge;
    private final UIImageView imageView;

    public BasketButton() {
        imageView = new UIImageView(UIImage.getImage("cart").newImage(UIImageRenderingMode.AlwaysTemplate));
        imageView.setTintColor(Colors.White);
        addSubview(imageView);

        badge = new BadgeView(new CGRect(20, 5, 0, 0));
        addSubview(badge);
    }

    public int getItemsCount() {
        return badge.getBadgeNumber();
    }

    public void setItemsCount(int count) {
        badge.setBadgeNumber(count);
    }

    public void updateItemsCount(int count) {
        setItemsCount(count);
        CAKeyframeAnimation pathAnimation = new CAKeyframeAnimation("transform");
        pathAnimation.setCalculationMode(CAAnimationCalculationMode.Paced);
        pathAnimation.setFillMode(CAFillMode.Forwards);
        pathAnimation.setTimingFunction(new CAMediaTimingFunction(CAMediaTimingFunctionName.EaseOut));
//        pathAnimation.setRemovedOnCompletion(false);
        pathAnimation.setDuration(0.2);

        CATransform3D transform = CATransform3D.createScale(2, 2, 1);
        pathAnimation.setValues(new NSArray<>(
                NSValue.valueOf(CATransform3D.Identity()),
                NSValue.valueOf(transform),
                NSValue.valueOf(CATransform3D.Identity())
                ));
        badge.getLayer().addAnimation(pathAnimation, "pulse");
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = getBounds();
        bounds.setX(bounds.getX() + PADDING + 15);
        bounds.setY(bounds.getY() + PADDING);
        bounds.setWidth(bounds.getWidth() - PADDING * 2);
        bounds.setHeight(bounds.getHeight() - PADDING * 2);
        imageView.setFrame(bounds);
    }
}
