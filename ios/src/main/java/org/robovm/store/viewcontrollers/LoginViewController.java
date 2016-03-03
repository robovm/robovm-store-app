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
package org.robovm.store.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIAlertViewDelegateAdapter;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.util.ProgressUI;
import org.robovm.store.views.LoginView;
import org.robovm.store.views.PrefillRoboVMAccountInstructionsView;

@SuppressWarnings("deprecation")
public class LoginViewController extends UIViewController {
    // TODO: Enter your RoboVM account email address here
    // If you do not have a RoboVM Account please sign up here:
    // https://account.robovm.com/#/register
    private static final String ROBOVM_ACCOUNT_EMAIL = "";

    private UIView contentView;
    private LoginView loginView;
    private UIScrollView scrollView;

    private final double keyboardOffset = 0;

    private Runnable loginSuccessListener;

    public LoginViewController() {
        setTitle("Log in");

        // This hides the back button text when you leave this View Controller
        getNavigationItem().setBackBarButtonItem(new UIBarButtonItem("", UIBarButtonItemStyle.Plain));
        setAutomaticallyAdjustsScrollViewInsets(false);
    }

    public boolean shouldShowInstructions() {
        return ROBOVM_ACCOUNT_EMAIL == null || ROBOVM_ACCOUNT_EMAIL.isEmpty();
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Login");
    }

    @Override
    public void viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews();

        CGRect bounds = getView().getBounds();
        contentView.setFrame(bounds);
        scrollView.setContentSize(bounds.getSize());

        // Resize Scroller for keyboard;
        bounds.setHeight(bounds.getHeight() - keyboardOffset);
        scrollView.setFrame(bounds);
    }

    @Override
    public void loadView() {
        super.loadView();

        getView().addSubview(scrollView = new UIScrollView(getView().getBounds()));
        if (shouldShowInstructions()) {
            scrollView.addSubview(contentView = new PrefillRoboVMAccountInstructionsView());
            GoogleAnalyticsService.getInstance().reportAnalyticEvent("Errors", "Missing Email", "Email", 1);
        } else {
            loginView = new LoginView(ROBOVM_ACCOUNT_EMAIL);
            loginView
                    .setLoginSuccessListener((v) -> login(ROBOVM_ACCOUNT_EMAIL, loginView.getPasswordField().getText()));
            scrollView.addSubview(contentView = loginView);
        }
    }

    private void login(String username, String password) {
        ProgressUI.show("Logging in...", this);

        RoboVMWebService.getInstance().authenticate(username, password, (success) -> {
            ProgressUI.hide();

            if (success) {
                if (loginSuccessListener != null) {
                    loginSuccessListener.run();
                }
            } else {
                UIAlertView alert = new UIAlertView("Could not log in!",
                        "Please verify your RoboVM account credentials and try again", null, "OK");
                alert.show();
                alert.setDelegate(new UIAlertViewDelegateAdapter() {
                    @Override
                    public void clicked(UIAlertView alertView, long buttonIndex) {
                        loginView.getPasswordField().setSelected(true);
                        loginView.getPasswordField().becomeFirstResponder();
                    }
                });
                GoogleAnalyticsService.getInstance().reportAnalyticEvent("Errors", "Login Failed", "Login", 1);
            }
        });
    }

    public void setLoginSuccessListener(Runnable listener) {
        this.loginSuccessListener = listener;
    }
}
