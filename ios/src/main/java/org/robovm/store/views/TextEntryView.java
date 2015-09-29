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
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UITextAutocapitalizationType;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegate;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.Action;

public class TextEntryView extends UIView implements UITextFieldDelegate {
    private Action<String> valueChangeListener;
    UITextField textField;

    public TextEntryView() {
        super(new CGRect(0, 0, 320, 44));

        textField = new UITextField() {
            {
                setBorderStyle(UITextBorderStyle.RoundedRect);
                setDelegate(TextEntryView.this);
            }
        };
    }

    public UIKeyboardType getKeyboardType() {
        return textField.getKeyboardType();
    }

    public void setKeyboardType(UIKeyboardType keyboardType) {
        textField.setKeyboardType(keyboardType);
    }

    public UITextAutocapitalizationType getAutocapitalizationType() {
        return textField.getAutocapitalizationType();
    }

    public void setAutocapitalizationType(UITextAutocapitalizationType type) {
        textField.setAutocapitalizationType(type);
    }

    public String getPlaceholder() {
        return textField.getPlaceholder();
    }

    public void setPlaceholder(String placeholder) {
        textField.setPlaceholder(placeholder);
    }

    public String getValue() {
        return textField.getText();
    }

    public void setValue(String value) {
        textField.setText(value);
        valueChangeListener.invoke(textField.getText());
    }

    public void setValueChangeListener(Action<String> listener) {
        this.valueChangeListener = listener;
    }

    @Override
    public void layoutSubviews() {
        final float sidePadding = 10f;
        final float topPadding = 5f;
        super.layoutSubviews();

        double width = getBounds().getWidth() - (sidePadding * 2);
        double height = getBounds().getHeight() - (topPadding * 2);

        textField.setFrame(new CGRect(sidePadding, topPadding, width, height));
    }

    @Override
    public boolean shouldBeginEditing(UITextField textField) {
        return true;
    }

    @Override
    public void didBeginEditing(UITextField textField) {}

    @Override
    public boolean shouldEndEditing(UITextField textField) {
        return true;
    }

    @Override
    public void didEndEditing(UITextField textField) {}

    @Override
    public boolean shouldChangeCharacters(UITextField textField, NSRange range, String string) {
        return true;
    }

    @Override
    public boolean shouldClear(UITextField textField) {
        return true;
    }

    @Override
    public boolean shouldReturn(UITextField textField) {
        textField.resignFirstResponder();
        return true;
    }
}
