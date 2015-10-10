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

import org.robovm.apple.uikit.NSLayoutAttribute;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSLayoutRelation;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.Colors;

public class PrefillRoboVMAccountInstructionsView extends UIView {
    public PrefillRoboVMAccountInstructionsView() {
        setBackgroundColor(Colors.White);

        UIImageView mockup = new UIImageView(UIImage.getImage("fill-details-instructions-mockup"));
        mockup.setTranslatesAutoresizingMaskIntoConstraints(false);

        addSubview(mockup);

        addConstraint(new NSLayoutConstraint(mockup, NSLayoutAttribute.CenterX, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.CenterX, 1, 0));
        addConstraint(new NSLayoutConstraint(mockup, NSLayoutAttribute.CenterY, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.CenterY, 1, -40));
    }
}
