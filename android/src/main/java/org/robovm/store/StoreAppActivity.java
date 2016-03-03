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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.api.RoboVMWebService.ActionWrapper;
import org.robovm.store.fragments.*;
import org.robovm.store.model.Product;
import org.robovm.store.util.Action;
import org.robovm.store.util.GoogleAnalyticsServiceAndroid;
import org.robovm.store.util.ImageCache;
import org.robovm.store.util.Images;

public class StoreAppActivity extends Activity {
    private int baseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Images.setScreenWidth(metrics.widthPixels);
        ImageCache.getInstance().setSaveLocation(getCacheDir().getAbsolutePath());

        super.onCreate(savedInstanceState);

        RoboVMWebService.getInstance().setup();
        ActionWrapper.WRAPPER = new RoboVMWebService.ActionWrapper() {
            @Override
            public <T> void invoke(Action<T> action, T result) {
                runOnUiThread(() -> action.invoke(result));
            }
        };

        GoogleAnalyticsService.setInstance(new GoogleAnalyticsServiceAndroid(getApplicationContext()));

        setContentView(R.layout.main);

        // Retain fragments so don't set home if state is stored.
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            ProductListFragment productFragment = new ProductListFragment();
            productFragment.setProductSelectionListener(this::showProductDetail);

            baseFragment = productFragment.getId();
            switchScreens(productFragment, false, true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("baseFragment", baseFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        baseFragment = savedInstanceState.getInt("baseFragment");
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.cart_menu_item:
            showBasket();
            return true;
        case android.R.id.home:
            // pop full backstack when going home.
            getFragmentManager().popBackStack(baseFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            setupActionBar();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setupActionBar(getFragmentManager().getBackStackEntryCount() != 0);
    }

    public int switchScreens(Fragment fragment) {
        return switchScreens(fragment, true, false);
    }

    public int switchScreens(Fragment fragment, boolean animated, boolean isRoot) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (animated) {
            transaction.setCustomAnimations(getInAnimationForFragment(fragment), getOutAnimationForFragment(fragment));
        }
        transaction.replace(R.id.contentArea, fragment);
        if (!isRoot) {
            transaction.addToBackStack(null);
        }

        setupActionBar(!isRoot);

        return transaction.commit();
    }

    private int getInAnimationForFragment(Fragment fragment) {
        int animIn = R.anim.enter;

        switch (fragment.getClass().getSimpleName()) {
        case "ProductDetailsFragment":
            animIn = R.anim.product_detail_in;
            break;
        case "BasketFragment":
            animIn = R.anim.basket_in;
            break;
        }
        return animIn;
    }

    private int getOutAnimationForFragment(Fragment fragment) {
        int animOut = R.anim.exit;

        switch (fragment.getClass().getSimpleName()) {
        case "ProductDetailsFragment":
            animOut = R.anim.product_detail_out;
            break;
        case "BasketFragment":
            break;
        }
        return animOut;
    }

    public void showProductDetail(Product product, int itemVerticalOffset) {
        ProductDetailsFragment productDetails = new ProductDetailsFragment(product, itemVerticalOffset);
        productDetails.setAddToBasketListener((order) -> {
            RoboVMWebService.getInstance().getBasket().add(order);
            setupActionBar();
        });
        switchScreens(productDetails);
    }

    public void setupActionBar() {
        setupActionBar(false);
    }

    public void setupActionBar(boolean showUp) {
        getActionBar().setDisplayHomeAsUpEnabled(showUp);
    }

    public void showBasket() {
        BasketFragment basket = new BasketFragment(RoboVMWebService.getInstance().getBasket());
        basket.setCheckoutListener(this::showLogin);
        switchScreens(basket);
    }

    public void showLogin() {
        LoginFragment login = new LoginFragment();
        login.setLoginSuccessListener(this::showAddress);
        switchScreens(login);
    }

    public void showAddress() {
        ShippingDetailsFragment shipping = new ShippingDetailsFragment(RoboVMWebService.getInstance().getCurrentUser());
        shipping.setOrderPlacedListener(this::orderCompleted);
        switchScreens(shipping);
    }

    public void orderCompleted() {
        getFragmentManager().popBackStack(baseFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        setupActionBar();

        switchScreens(new BragFragment(), true, true);
    }
}
