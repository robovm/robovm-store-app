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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIControl.OnTouchUpInsideListener;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.store.util.Colors;

public class BottomButtonView extends BrightlyBlurredView {
    public static final float HEIGHT = 75;
    private static final float PADDING = 15;

    private ImageButton button;

    public BottomButtonView() {
        addSubview(button = new ImageButton());
        button.getLayer().setBackgroundColor(Colors.Green.getCGColor());
        button.getLayer().setCornerRadius(5f);
        button.setFont(UIFont.getBoldSystemFont(UIFont.getButtonFontSize()));
        button.sizeToFit();
        setTintColor(Colors.White);
        setAccentColorIntensity(0f);
    }

    public BottomButtonView(String text, OnTouchUpInsideListener tapListener) {
        this();
        setButtonText(text);
        setButtonTapListener(tapListener);
    }

    public String getButtonText() {
        return button.getText();
    }

    public void setButtonText(String text) {
        button.setText(text);
    }

    public void setButtonImage(UIImage image) {
        button.setImage(image);
    }

    public void setButtonTapListener(OnTouchUpInsideListener listener) {
        button.addOnTouchUpInsideListener(listener);
    }

    public ImageButton getButton() {
        return button;
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        CGRect bounds = getBounds();
        bounds.setX(PADDING);
        bounds.setY(PADDING);
        bounds.setWidth(bounds.getWidth() - PADDING * 2);
        bounds.setHeight(bounds.getHeight() - PADDING * 2);
        button.setFrame(bounds);
    }
}
