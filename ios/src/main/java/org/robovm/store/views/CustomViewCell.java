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
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIView;

public class CustomViewCell extends UITableViewCell {
    private final UIView child;
    private UIEdgeInsets padding = new UIEdgeInsets();
    private boolean resizeChild = true;

    public CustomViewCell(UIView child) {
        super(UITableViewCellStyle.Default, "CustomViewCell");

        this.child = child;

        setSelectionStyle(UITableViewCellSelectionStyle.None);
        resizeChild = true;
        padding = new UIEdgeInsets();

        CGRect frame = child.getFrame();
        frame.setHeight(frame.getY() + frame.getHeight());
        frame.setY(0);
        setFrame(frame);
        getContentView().addSubview(child);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        if (!resizeChild) {
            child.setCenter(getContentView().getCenter());
            return;
        }
        CGRect bounds = getContentView().getBounds();
        bounds.setX(bounds.getX() + padding.getLeft());
        bounds.setY(bounds.getY() + padding.getTop());
        bounds.setHeight(bounds.getHeight() - (padding.getBottom() + padding.getTop()));
        bounds.setWidth(bounds.getWidth() - (padding.getLeft() + padding.getRight()));
        child.setFrame(bounds);
    }
}
