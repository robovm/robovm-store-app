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

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSLayoutAttribute;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSLayoutRelation;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.Action;
import org.robovm.store.util.Colors;

public class LoginView extends UIView {
    private static final CGSize GRAVATAR_SIZE = new CGSize(85, 85);

    private UIImageView gravatarView;
    private UITextField emailField;
    private UITextField passwordField;

    private Action<LoginView> didLogin;

    public LoginView(String robovmAccountEmail) {
        setBackgroundColor(Colors.White);

        gravatarView = new UIImageView(new CGRect(CGPoint.Zero(), GRAVATAR_SIZE));
        gravatarView.setTranslatesAutoresizingMaskIntoConstraints(false);
        gravatarView.setImage(UIImage.getImage("user-default-avatar"));

        gravatarView.getLayer().setCornerRadius(GRAVATAR_SIZE.getWidth() / 2);
        gravatarView.getLayer().setMasksToBounds(true);
        addSubview(gravatarView);

        displayGravatar(robovmAccountEmail);

        addConstraint(new NSLayoutConstraint(gravatarView, NSLayoutAttribute.Top, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.Top, 1, 90));
        addConstraint(new NSLayoutConstraint(gravatarView, NSLayoutAttribute.CenterX, NSLayoutRelation.Equal, this,
                NSLayoutAttribute.CenterX, 1, 0));
        addConstantSizeConstraints(gravatarView, GRAVATAR_SIZE);

        emailField = new UITextField(new CGRect(10, 10, 300, 30));
        emailField.setBorderStyle(UITextBorderStyle.RoundedRect);
        emailField.setText(robovmAccountEmail);
        emailField.setTranslatesAutoresizingMaskIntoConstraints(false);
        emailField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldBeginEditing(UITextField textField) {
                return false;
            }
        });
        addSubview(emailField);

        addConstraint(new NSLayoutConstraint(emailField, NSLayoutAttribute.Top, NSLayoutRelation.Equal, gravatarView,
                NSLayoutAttribute.Bottom, 1, 30));
        addConstraint(new NSLayoutConstraint(emailField, NSLayoutAttribute.CenterX, NSLayoutRelation.Equal,
                gravatarView, NSLayoutAttribute.CenterX, 1, 0));

        CGSize textSize = NSString.getSize("hello",
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(12)));
        addConstantSizeConstraints(emailField, new CGSize(260, textSize.getHeight() + 16));

        passwordField = new UITextField(new CGRect(10, 10, 300, 30));
        passwordField.setBorderStyle(UITextBorderStyle.RoundedRect);
        passwordField.setSecureTextEntry(true);
        passwordField.setTranslatesAutoresizingMaskIntoConstraints(false);
        passwordField.setReturnKeyType(UIReturnKeyType.Go);
        passwordField.setPlaceholder("Password");
        addSubview(passwordField);

        addConstraint(new NSLayoutConstraint(passwordField, NSLayoutAttribute.Top, NSLayoutRelation.Equal, emailField,
                NSLayoutAttribute.Bottom, 1, 10));
        addConstraint(new NSLayoutConstraint(passwordField, NSLayoutAttribute.CenterX, NSLayoutRelation.Equal,
                emailField, NSLayoutAttribute.CenterX, 1, 0));
        addConstantSizeConstraints(passwordField, new CGSize(260, textSize.getHeight() + 16));

        passwordField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldReturn(UITextField textField) {
                textField.resignFirstResponder();
                if (didLogin != null) {
                    didLogin.invoke(LoginView.this);
                }
                return true;
            }
        });

        passwordField.becomeFirstResponder();
    }

    private void displayGravatar(String email) {
//        NSData data;
//
//        try {
//            data = Gravatar.getImageData(email, GRAVATAR_SIZE.getWidth() * 2);
//        } catch (Exception e) {
//            return;
//        }
        // TODO
//        gravatarView.setImage(new UIImage(data));
    }

    private void addConstantSizeConstraints(UIView view, CGSize size) {
        addConstraint(new NSLayoutConstraint(view, NSLayoutAttribute.Width, NSLayoutRelation.Equal, null,
                NSLayoutAttribute.NotAnAttribute, 1, size.getWidth()));
        addConstraint(new NSLayoutConstraint(view, NSLayoutAttribute.Height, NSLayoutRelation.Equal, null,
                NSLayoutAttribute.NotAnAttribute, 1, size.getHeight()));
    }

    public UITextField getEmailField() {
        return emailField;
    }

    public UITextField getPasswordField() {
        return passwordField;
    }

    public void setLoginSuccessListener(Action<LoginView> listener) {
        this.didLogin = listener;
    }
}
