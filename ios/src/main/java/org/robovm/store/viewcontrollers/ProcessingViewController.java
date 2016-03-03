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

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.social.SLComposeViewController;
import org.robovm.apple.social.SLServiceType;
import org.robovm.apple.uikit.NSLineBreakMode;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageRenderingMode;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAnimationCurve;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.api.ValidationError;
import org.robovm.store.model.User;
import org.robovm.store.util.Colors;
import org.robovm.store.util.ImageCache;
import org.robovm.store.views.ImageButton;

@SuppressWarnings("deprecation")
public class ProcessingViewController extends UIViewController {
    private final User user;
    private ProcessingView processView;

    private Runnable orderPlacedListener;

    public ProcessingViewController(User user) {
        this.user = user;

        setTitle("Processing");
        getNavigationItem().setHidesBackButton(true);
    }

    @Override
    public void loadView() {
        super.loadView();

        getView().setBackgroundColor(Colors.Gray);
        getView().addSubview(processView = new ProcessingView(this::processOrder));
    }

    @Override
    public void viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews();
        processView.setFrame(getView().getBounds());
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);
        processOrder();
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        if (getNavigationController() != null) {
            getNavigationController().getNavigationBar().setBarStyle(UIBarStyle.Black);
        }
    }

    private void processOrder() {
        processView.spinGear();

        RoboVMWebService.getInstance().placeOrder(user, (response) -> {
            if (response.isSuccess()) {
                RoboVMWebService.getInstance().getBasket().clear();

                processView.setStatus("Your order has been placed!");
                processView.stopGear();

                showSuccess();
            } else {
                List<ValidationError> errors = response.getErrors();
                String alertMessage = "An unexpected error occurred! Please try again later!";

                if (errors != null) { // We handle only the first error.
                    ValidationError error = errors.get(0);

                    String message = error.getMessage();
                    String field = error.getField();
                    if (field == null) {
                        alertMessage = message;
                    } else {
                        switch (field) {
                        case "firstName":
                            alertMessage = "First name is required";
                            break;
                        case "lastName":
                            alertMessage = "Last name is required";
                            break;
                        case "address1":
                            alertMessage = "Address is required";
                            break;
                        case "city":
                            alertMessage = "City is required";
                            break;
                        case "zipCode":
                            alertMessage = "ZIP code is required";
                            break;
                        case "phone":
                            alertMessage = "Phone number is required";
                            break;
                        case "country":
                            alertMessage = "Country is required";
                            break;
                        default:
                            alertMessage = message;
                            break;
                        }
                    }
                }
                showErrorAlert(alertMessage);
                getNavigationController().popViewController(true);
            }
        });
    }

    private void showErrorAlert(String message) {
        new UIAlertView("Error", message, null, "OK").show();
    }

    private void showSuccess() {
        SuccessView view = new SuccessView();
        view.setFrame(getView().getBounds());
        getView().addSubview(view);
        UIView.animate(.3, () -> processView.setAlpha(0));
        view.animateIn();
    }

    private void tweet() {
        if (SLComposeViewController.isAvailable(SLServiceType.Twitter)) {
            RoboVMWebService
                    .getInstance()
                    .getProducts(
                            (products) -> {
                                SLComposeViewController svc = new SLComposeViewController(SLServiceType.Twitter);
                                if (products != null && products.size() > 0) {
                                    Collections.shuffle(products);

                                    String imageUrl = products.get(0).getImageUrl();
                                    File image = ImageCache.getInstance().getImage(imageUrl);
                                    if (image != null) {
                                        svc.addImage(new UIImage(image));
                                    }
                                }
                                svc.addURL(new NSURL("http://robovm.com"));
                                svc.setInitialText(
                                        "I just built a native iOS app with Java using #RoboVM and all I got was this free T-shirt!");
                                presentViewController(svc, true, null);
                            });
            GoogleAnalyticsService.getInstance().reportAnalyticEvent("Action", "Tweet", "tweet", 1);
        }
    }

    public void setOrderPlacedListener(Runnable listener) {
        this.orderPlacedListener = listener;
    }

    static class ProcessingView extends UIView {
        private final UIImageView gear;
        private final UILabel status;
        private final ImageButton tryAgain;

        private CGRect lastFrame;

        private boolean isSpinning;
        private int currentRotation;

        public ProcessingView(Runnable tryAgainListener) {
            gear = new UIImageView(UIImage.getImage("gear"));
            addSubview(gear);

            status = new UILabel();
            status.setBackgroundColor(Colors.Clear);
            status.setTextAlignment(NSTextAlignment.Center);
            status.setTextColor(Colors.White);
            status.setNumberOfLines(0);
            status.setLineBreakMode(NSLineBreakMode.WordWrapping);
            status.setContentMode(UIViewContentMode.Top);
            addSubview(status);

            tryAgain = new ImageButton();
            tryAgain.setTintColor(Colors.White);
            tryAgain.setText("Try Again");
            tryAgain.addOnTouchUpInsideListener((b, e) -> {
                animate(.3, tryAgain::removeFromSuperview);
                if (tryAgainListener != null) {
                    tryAgainListener.run();
                }
            });
        }

        @Override
        public void layoutSubviews() {
            super.layoutSubviews();

            // Only re-layout if the bounds changed. If not the gear bounces
            // around during rotation.
            CGRect bounds = getBounds();
            if (!bounds.equals(lastFrame)) {
                lastFrame = bounds;
                gear.setCenter(new CGPoint(bounds.getMidX(), bounds.getMidY() - (gear.getFrame().getHeight() / 2)));
            }
        }

        public void spinGear() {
            setStatus("Processing Order...");
            if (!isSpinning) {
                isSpinning = true;
                startGear();
            }
        }

        private void startGear() {
            animate(.6, () -> {
                UIView.setAnimationCurve(UIViewAnimationCurve.EaseIn);
                gear.setTransform(getNextRotation(30));
            }, (s) -> spin());
        }

        private void spin() {
            if (!isSpinning) {
                stopGear();
            } else {
                animate(.2, () -> {
                    UIView.setAnimationCurve(UIViewAnimationCurve.Linear);
                    gear.setTransform(getNextRotation(10));
                }, (s) -> spin());
            }
        }

        private void stopGear() {
            animate(.6, () -> {
                UIView.setAnimationCurve(UIViewAnimationCurve.EaseOut);
                gear.setTransform(getNextRotation(30));
            }, (s) -> animationEnded());
        }

        private CGAffineTransform getNextRotation(int increment) {
            currentRotation += increment;
            if (currentRotation >= 360) {
                currentRotation -= 360;
            }

            double rad = Math.PI * currentRotation / 180;
            return CGAffineTransform.createRotation(rad);
        }

        public String getStatus() {
            return status.getText();
        }

        public void setStatus(String status) {
            this.status.setText(status != null ? status : "");

            CGRect statusFrame = new CGRect(10, gear.getFrame().getY() + gear.getFrame().getHeight() + 10, getBounds()
                    .getWidth() - 20, getBounds().getHeight() - gear.getFrame().getY() - gear.getFrame().getHeight());
            statusFrame.setHeight(this.status.getSizeThatFits(statusFrame.getSize()).getHeight());
            this.status.setFrame(statusFrame);
        }

        private void animationEnded() {
            isSpinning = false;
        }

        public void showTryAgain() {
            CGPoint center = new CGPoint(getBounds().getMidX(), getBounds().getHeight()
                    - tryAgain.getFrame().getHeight() / 2 - 10);
            tryAgain.setCenter(center);
            tryAgain.setAlpha(0);
            addSubview(tryAgain);
            animate(.3, () -> tryAgain.setAlpha(1));
        }
    }

    class SuccessView extends UIView {
        private final UIImageView check;
        private final UILabel label1;
        private final UILabel label2;
        private final ImageButton twitter;
        private final ImageButton done;

        private float yOffset = 20;

        public SuccessView() {
            check = new UIImageView(UIImage.getImage("success"));
            check.setAlpha(0);
            addSubview(check);

            label1 = new UILabel();
            label1.setText("Order Complete");
            label1.setTextAlignment(NSTextAlignment.Center);
            label1.setFont(UIFont.getBoldSystemFont(25));
            label1.setTextColor(Colors.White);
            label1.setAlpha(0);
            addSubview(label1);
            label1.sizeToFit();

            label2 = new UILabel();
            label2.setText("We've received your order and we'll email you as soon as your items ship.");
            label2.setTextAlignment(NSTextAlignment.Center);
            label2.setFont(UIFont.getSystemFont(17));
            label2.setNumberOfLines(0);
            label2.setLineBreakMode(NSLineBreakMode.WordWrapping);
            label2.setTextColor(Colors.White);
            label2.setAlpha(0);
            addSubview(label2);
            label2.sizeToFit();

            twitter = new ImageButton();
            twitter.setText("Brag on Twitter");
            twitter.setImage(UIImage.getImage("twitter").newImage(UIImageRenderingMode.AlwaysTemplate));
            twitter.setTintColor(Colors.White);
            twitter.setFont(UIFont.getSystemFont(20));
            twitter.setAlpha(0);
            twitter.addOnTouchUpInsideListener((b, e) -> tweet());
            if (SLComposeViewController.isAvailable(SLServiceType.Twitter)) {
                addSubview(twitter);
            }

            done = new ImageButton();
            done.setText("Done");
            done.setTintColor(Colors.White);
            done.setFont(UIFont.getSystemFont(20));
            done.setAlpha(0);
            addSubview(done);

            done.addOnTouchUpInsideListener((b, e) -> orderPlacedListener.run());
        }

        @Override
        public void layoutSubviews() {
            super.layoutSubviews();

            CGRect bounds = getBounds();

            final float padding = 10;
            double y = bounds.getHeight() / 3;
            check.setCenter(new CGPoint(bounds.getMidX(), y - check.getFrame().getHeight() / 2));

            CGRect frame = label1.getFrame();
            frame.setX(padding);
            frame.setY(check.getFrame().getY() + check.getFrame().getHeight() + padding + yOffset);
            frame.setWidth(bounds.getWidth() - (padding * 2));
            label1.setFrame(frame);

            frame.setY(frame.getY() + frame.getHeight() + padding);
            frame.setHeight(label2.getSizeThatFits(new CGSize(frame.getWidth(), bounds.getHeight())).getHeight());
            label2.setFrame(frame);

            frame = done.getFrame();
            frame.setY(bounds.getHeight() - padding - frame.getHeight());
            frame.setX((bounds.getWidth() - frame.getWidth()) / 2);
            done.setFrame(frame);

            frame.setY(frame.getY() - (frame.getHeight() + padding));
            twitter.setFrame(frame);
        }

        public void animateIn() {
            yOffset = 20;
            layoutSubviews();

            animate(.1, () -> {
                UIView.setAnimationCurve(UIViewAnimationCurve.EaseOut);
                check.setAlpha(1);
                twitter.setAlpha(1);
                done.setAlpha(1);
            }, (s) -> animate(.2, () -> {
                UIView.setAnimationCurve(UIViewAnimationCurve.EaseInOut);
                yOffset = 0;
                layoutSubviews();
                label1.setAlpha(1);
                label2.setAlpha(1);
            }));
        }
    }
}
