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
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.store.util.Colors;

public class BadgeView extends UILabel {
    private final float height = 14;
    private CGSize numberSize;
    private int badgeNumber;

    public BadgeView(CGRect rect) {
        super(rect);
        setup();
    }

    public BadgeView() {
        setup();
    }

    private void setup() {
        setBackgroundColor(Colors.Clear);
        setTextColor(Colors.Green);
        setFont(UIFont.getBoldSystemFont(10f));
        setUserInteractionEnabled(false);
        getLayer().setCornerRadius(height / 2);
        getLayer().setBackgroundColor(Colors.White.getCGColor());
        setTextAlignment(NSTextAlignment.Center);
    }

    public int getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(int badgeNumber) {
        this.badgeNumber = badgeNumber;
        setText(String.valueOf(badgeNumber));
        calculateSize();
        setAlpha(badgeNumber > 0 ? 1 : 0);
        setNeedsDisplay();
    }

    private void calculateSize() {
        numberSize = NSString.getSize(String.valueOf(badgeNumber),
                new NSAttributedStringAttributes().setFont(getFont()));
        setFrame(new CGRect(getFrame().getOrigin(), new CGSize(Math.max(numberSize.getWidth(), height), height)));
    }
}
