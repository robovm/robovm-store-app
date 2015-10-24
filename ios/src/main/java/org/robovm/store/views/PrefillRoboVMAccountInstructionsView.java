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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSMutableAttributedString;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.uikit.*;
import org.robovm.store.util.Colors;

public class PrefillRoboVMAccountInstructionsView extends UIView {
    public PrefillRoboVMAccountInstructionsView() {
        setBackgroundColor(Colors.White);

        UIImageView tools = new UIImageView(UIImage.getImage("tools"));
        tools.setTranslatesAutoresizingMaskIntoConstraints(false);
        addSubview(tools);

        final double fontSize = 13;
        final UIFont codeFont = UIFont.getFont("Courier", fontSize);
        final UIFont boldCodeFont = UIFont.getFont("Courier-Bold", fontSize);
        final UIFont boldItalicCodeFont = UIFont.getFont("Courier-BoldOblique", fontSize);

        UILabel code = new UILabel(new CGRect(0, getFrame().getWidth(), 0, 0));
        code.setTranslatesAutoresizingMaskIntoConstraints(false);
        code.setFont(codeFont);

        NSMutableAttributedString str = new NSMutableAttributedString("static String ROBOVM_ACCOUNT_EMAIL = \"...\";");
        str.addAttributes(new NSAttributedStringAttributes().setForegroundColor(UIColor.fromRGBA(0, 0, 128 / 255f, 1))
                .setFont(boldCodeFont), new NSRange(0, 6));
        str.addAttributes(new NSAttributedStringAttributes()
                .setForegroundColor(UIColor.fromRGBA(102 / 255f, 14 / 255f, 122 / 255f, 1))
                .setFont(boldItalicCodeFont), new NSRange(14, 20));
        str.addAttributes(new NSAttributedStringAttributes()
                .setForegroundColor(UIColor.fromRGBA(0, 128 / 255f, 0, 1))
                .setFont(boldCodeFont), new NSRange(37, 5));

        code.setAttributedText(str);
        addSubview(code);

        UILabel instructions = new UILabel();
        instructions.setTranslatesAutoresizingMaskIntoConstraints(false);
        instructions.setLineBreakMode(NSLineBreakMode.WordWrapping);
        instructions.setFont(UIFont.getSystemFont(14));
        instructions.setTextAlignment(NSTextAlignment.Center);
        instructions.setNumberOfLines(3);
        instructions.setText(
                "A small task is required to get your free shirt. Please add your RoboVM account email address to LoginFragment.java then revisit this screen.");
        addSubview(instructions);

        addConstraint(new NSLayoutConstraint(tools, NSLayoutAttribute.CenterX, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.CenterX, 1, 0));
        addConstraint(new NSLayoutConstraint(tools, NSLayoutAttribute.CenterY, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.CenterY, 1, -80));
        addConstraint(new NSLayoutConstraint(code, NSLayoutAttribute.CenterX, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.CenterX, 1, 0));
        addConstraint(new NSLayoutConstraint(code, NSLayoutAttribute.CenterY, NSLayoutRelation.Equal, tools,
                NSLayoutAttribute.CenterY, 1, 80));
        addConstraint(new NSLayoutConstraint(code, NSLayoutAttribute.Width, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.Width, 0.9, 0));
        addConstraint(new NSLayoutConstraint(instructions, NSLayoutAttribute.CenterX, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.CenterX, 1, 0));
        addConstraint(new NSLayoutConstraint(instructions, NSLayoutAttribute.CenterY, NSLayoutRelation.Equal, code,
                NSLayoutAttribute.CenterY, 1, 80));
        addConstraint(new NSLayoutConstraint(instructions, NSLayoutAttribute.Width, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.Width, 0.9, 0));
    }
}
