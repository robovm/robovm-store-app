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

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIViewTintAdjustmentMode;

public class ImageButton extends UIControl {
    private final UIImageView imageView;
    private final UILabel label;

    public ImageButton() {
        super(new CGRect(0, 0, 250, 50));

        imageView = new UIImageView();
        addSubview(imageView);

        label = new UILabel();
        label.setTextColor(getTintColor());
        addSubview(label);

        CALayer layer = getLayer();
        layer.setBorderColor(getTintColor().getCGColor());
        layer.setBorderWidth(1f);
        layer.setCornerRadius(5f);
    }

    public UIImageView getImageView() {
        return imageView;
    }

    @Override
    public void tintColorDidChange() {
        super.tintColorDidChange();
        UIColor tintColor = getTintColor();

        getLayer().setBorderColor(tintColor.getCGColor());
        label.setTextColor(tintColor);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        final float padding = 10f;
        CGRect bounds = getBounds();
        CGSize imageSize = imageView.getSizeThatFits(bounds.getSize());

        double availableWidth = bounds.getWidth() - padding * 3 - imageSize.getWidth();
        CGSize stringSize = label.getSizeThatFits(new CGSize(availableWidth, bounds.getHeight() - padding * 2));

        availableWidth = bounds.getWidth();
        availableWidth -= stringSize.getWidth();
        availableWidth -= imageSize.getWidth();

        double x = availableWidth / 2;

        CGRect frame = new CGRect(new CGPoint(x, bounds.getMidY() - imageSize.getHeight() / 2), imageSize);
        imageView.setFrame(frame);

        frame.setX(frame.getX() + frame.getWidth() + (imageSize.getWidth() > 0 ? padding : 0));
        frame.setSize(stringSize);
        frame.setHeight(bounds.getHeight());
        frame.setY(0);
        label.setFrame(frame);
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);
    }

    public UIImage getImage() {
        return imageView.getImage();
    }

    public void setImage(UIImage image) {
        imageView.setImage(image);
    }

    public UIFont getFont() {
        return label.getFont();
    }

    public void setFont(UIFont font) {
        label.setFont(font);
    }

    @Override
    public void touchesBegan(NSSet<UITouch> touches, UIEvent event) {
        super.touchesBegan(touches, event);
        setTintAdjustmentMode(UIViewTintAdjustmentMode.Dimmed);
    }

    @Override
    public void touchesEnded(NSSet<UITouch> touches, UIEvent event) {
        super.touchesEnded(touches, event);
        setTintAdjustmentMode(UIViewTintAdjustmentMode.Automatic);
    }

    @Override
    public void touchesCancelled(NSSet<UITouch> touches, UIEvent event) {
        super.touchesCancelled(touches, event);
        setTintAdjustmentMode(UIViewTintAdjustmentMode.Automatic);
    }
}
