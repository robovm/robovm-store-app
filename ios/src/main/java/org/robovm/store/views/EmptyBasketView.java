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

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.Colors;

public class EmptyBasketView extends UIView {
    private UIImageView image;
    private UILabel text;

    public EmptyBasketView() {
        setBackgroundColor(Colors.White);
        addSubview(image = new UIImageView(UIImage.getImage("empty-basket")));
        addSubview(text = new UILabel());
        text.setText("Your basket is empty");
        text.setTextColor(Colors.LightGray);
        text.setFont(UIFont.getBoldSystemFont(20f));
        text.setTextAlignment(NSTextAlignment.Center);
        text.setBackgroundColor(Colors.Clear);
        text.sizeToFit();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        CGPoint center = new CGPoint(getBounds().getMidX(), getBounds().getMidY());
        image.setCenter(center);

        center.setY(center.getY() + (image.getFrame().getHeight() + text.getFrame().getHeight()) / 2);
        text.setCenter(center);
    }
}
