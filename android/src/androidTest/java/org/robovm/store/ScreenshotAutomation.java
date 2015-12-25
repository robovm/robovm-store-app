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
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.fragments.ShippingDetailsFragment;
import org.robovm.store.model.Order;
import org.robovm.store.model.Product;
import org.robovm.store.model.User;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Automatically takes screenshots of the different fragments of the app.
 * Screenshots are stored in EXTERNAL_STORAGE/screenshots, they are transfered to android/screenshots
 * Animations MUST BE disabled, go to ->Settings -> Developer Options and disable them.
 */
public class ScreenshotAutomation
        extends ActivityInstrumentationTestCase2<StoreAppActivity> {

    private StoreAppActivity mActivity;

    public ScreenshotAutomation() {
        super(StoreAppActivity.class);
    }

    @UiThreadTest
    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
        onView(withText(mActivity.getString(R.string.app_name))).perform(click()); //Unfocus the app title
        clearBasket();
    }

    private void clearBasket() {
        try {
            runTestOnUiThread(() -> RoboVMWebService.getInstance().getBasket().clear());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        RoboVMWebService.getInstance().getBasket().getOrders().clear();
    }

    @UiThreadTest
    public void testTakeScreenshotOfProductList() {
        SystemClock.sleep(5000);
        takeScreenshot("product_list", mActivity);
    }

    @UiThreadTest
    public void testTakeScreenshotOfProductDetail() {
        onData(anything())
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0)
                .perform(click());
        SystemClock.sleep(2000);
        takeScreenshot("product_detail", mActivity);
    }

    @UiThreadTest
    public void testTakeScreenshotOfBasket() throws Throwable {
        runTestOnUiThread(() -> {
                    RoboVMWebService.getInstance().getProducts((products) -> {
                        for (Product product : products) {
                            RoboVMWebService.getInstance().getBasket().add(new Order(product));
                        }
                    });
                }
        );
        SystemClock.sleep(15000);
        onView(withId(R.id.cart_menu_item)).perform(click());
        takeScreenshot("basket_list", mActivity);
    }

    @UiThreadTest
    public void testTakeScreenshotOfInstructions() throws Throwable {
        runTestOnUiThread(mActivity::showLogin);
        SystemClock.sleep(2000);
        takeScreenshot("login_without_instructions", mActivity);
    }

    @UiThreadTest
    public void testTakeScreenshotOfShippingScreen() throws Throwable {
        runTestOnUiThread(() -> {
                    User u = new User();
                    u.setCountry("");
                    ShippingDetailsFragment shipping = new ShippingDetailsFragment(u);
                    mActivity.switchScreens(shipping);
                }
        );

        SystemClock.sleep(2000);
        takeScreenshot("shipping_screen", mActivity);
    }

    @UiThreadTest
    public void testTakeScreenshotOfBragScreen() throws Throwable {
        runTestOnUiThread(mActivity::orderCompleted);
        SystemClock.sleep(2000);
        takeScreenshot("brag_screen", mActivity);
    }


    private void takeScreenshot(String name, Activity mActivity) {
        // Take screenshots, save one with a timestamp, the other without, so we can easily pull it back via adb.
        //Some screenshots can't be taken if it isn't run from the ui-thread!
        try {
            runTestOnUiThread(() -> {
                ScreenshotTaker.takeScreenshotWithTimestamp(name, mActivity);
                ScreenshotTaker.takeScreenshot("latest_" + name, mActivity);
            });
        } catch (Throwable t) {
            System.err.println("Failed to take screenshot, name: " + name);
            t.printStackTrace();
        }
    }

}