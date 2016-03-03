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
package org.robovm.store;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSPathUtilities;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIAppearance;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UINavigationBar;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UINavigationControllerDelegateAdapter;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.api.RoboVMWebService.ActionWrapper;
import org.robovm.store.model.Product;
import org.robovm.store.util.Action;
import org.robovm.store.util.Colors;
import org.robovm.store.util.GoogleAnalyticsServiceiOS;
import org.robovm.store.util.ImageCache;
import org.robovm.store.viewcontrollers.BasketViewController;
import org.robovm.store.viewcontrollers.LoginViewController;
import org.robovm.store.viewcontrollers.ProcessingViewController;
import org.robovm.store.viewcontrollers.ProductDetailViewController;
import org.robovm.store.viewcontrollers.ProductListViewController;
import org.robovm.store.viewcontrollers.ShippingAddressViewController;
import org.robovm.store.views.BasketButton;

public class StoreApp extends UIApplicationDelegateAdapter {
    private static StoreApp instance;

    private StoreApp() {}

    public static StoreApp getInstance() {
        return instance;
    }

    private UINavigationController navigation;
    private BasketButton basketButton;

    @SuppressWarnings("deprecation")
    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        instance = this;

        ImageCache.getInstance().setSaveLocation(NSPathUtilities.getHomeDirectory() + "/Documents/");
        RoboVMWebService.getInstance().setup();
        ActionWrapper.WRAPPER = new ActionWrapper() {
            @Override
            public <T> void invoke(Action<T> action, T result) {
                NSOperationQueue.getMainQueue().addOperation(() -> {
                    action.invoke(result);
                });
            }
        };

        UIApplication.getSharedApplication().setStatusBarStyle(UIStatusBarStyle.LightContent, false);

        UIWindow window = new UIWindow(UIScreen.getMainScreen().getBounds());
        UIAppearance.getAppearance(UINavigationBar.class).setTitleTextAttributes(
                new NSAttributedStringAttributes().setForegroundColor(UIColor.white()));

        ProductListViewController productViewController = new ProductListViewController();
        productViewController.setProductSelectionListener(this::showProductDetail);
        navigation = new UINavigationController(productViewController);

        navigation.getNavigationBar().setTintColor(Colors.White);
        navigation.getNavigationBar().setBarTintColor(Colors.Green);

        navigation.setDelegate(new UINavigationControllerDelegateAdapter() {
            @Override
            public void didShowViewController(UINavigationController navigationController,
                    UIViewController viewController, boolean animated) {

                // Remove the LoginViewController from the stack, if we are
                // already logged in.
                if (viewController instanceof ShippingAddressViewController) {
                    NSArray<UIViewController> vcs = navigationController.getViewControllers();
                    if (RoboVMWebService.getInstance().isAuthenticated()
                            && vcs.get(vcs.size() - 2) instanceof LoginViewController) {
                        NSMutableArray<UIViewController> array = new NSMutableArray<>(vcs);
                        array.remove(vcs.size() - 2);
                        navigationController.setViewControllers(array, false);
                    }
                }
            }
        });

        window.setRootViewController(navigation);
        window.makeKeyAndVisible();

        setWindow(window);

        return true;
    }

    public void showProductDetail(Product product) {
        ProductDetailViewController productDetails = new ProductDetailViewController(product);
        productDetails.setAddToBasketListener((order) -> {
            RoboVMWebService.getInstance().getBasket().add(order);
            updateProductsCount();
        });
        navigation.pushViewController(productDetails, true);
    }

    public void showBasket() {
        BasketViewController basketViewController = new BasketViewController(
                RoboVMWebService.getInstance().getBasket());
        basketViewController.setCheckoutListener(() -> {
            if (RoboVMWebService.getInstance().isAuthenticated()) {
                showAddress();
            } else {
                showLogin();
            }
        });
        navigation.pushViewController(basketViewController, true);
    }

    public void showLogin() {
        LoginViewController loginViewController = new LoginViewController();
        loginViewController.setLoginSuccessListener(this::showAddress);
        navigation.pushViewController(loginViewController, true);
    }

    public void showAddress() {
        ShippingAddressViewController addressViewController = new ShippingAddressViewController(RoboVMWebService
                .getInstance().getCurrentUser());
        addressViewController.setShippingCompleteListener(this::processOrder);
        navigation.pushViewController(addressViewController, true);
    }

    public void processOrder() {
        ProcessingViewController processing = new ProcessingViewController(RoboVMWebService.getInstance()
                .getCurrentUser());
        processing.setOrderPlacedListener(this::orderCompleted);
        navigation.pushViewController(processing, true);
    }

    private void orderCompleted() {
        navigation.popToRootViewController(true);
    }

    public UIBarButtonItem createBasketButton() {
        if (basketButton == null) {
            basketButton = new BasketButton();
            basketButton.setFrame(new CGRect(0, 0, 44, 44));
            basketButton.addOnTouchUpInsideListener((button, event) -> showBasket());
        }

        basketButton.setItemsCount(RoboVMWebService.getInstance().getBasket().size());
        return new UIBarButtonItem(basketButton);
    }

    public void updateProductsCount() {
        basketButton.updateItemsCount(RoboVMWebService.getInstance().getBasket().size());
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            GoogleAnalyticsService.setInstance(new GoogleAnalyticsServiceiOS());
            UIApplication.main(args, null, StoreApp.class);
        }
    }
}
