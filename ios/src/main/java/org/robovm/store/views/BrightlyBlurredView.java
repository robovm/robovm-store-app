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
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.Colors;

public class BrightlyBlurredView extends UIView {
    private CALayer blurLayer, accentLayer;
    private final UIView accentView;
    private final UIToolbar toolbar;

    public BrightlyBlurredView() {
        toolbar = new UIToolbar();
        toolbar.setOpaque(true);
        getLayer().addSublayer(blurLayer = toolbar.getLayer());

        accentView = new UIView();
        accentView.setBackgroundColor(getTintColor());
        accentView.setAlpha(.7f);
        accentView.setOpaque(false);

        blurLayer.insertSublayerAt(accentLayer = accentView.getLayer(), 1);
        getLayer().setMasksToBounds(true);
        setBackgroundColor(Colors.Clear);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = getBounds();
        accentLayer.setFrame(bounds);
        blurLayer.setFrame(bounds);
    }

    public double getAccentColorIntensity() {
        return accentView.getAlpha();
    }

    public void setAccentColorIntensity(float value) {
        accentView.setAlpha(value);
    }

    @Override
    public void setTintColor(UIColor color) {
        super.setTintColor(color);
        toolbar.setBarTintColor(color);
        accentView.setBackgroundColor(color);
    }
}
