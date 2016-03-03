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

package org.robovm.store.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.robovm.store.R;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.model.Basket;
import org.robovm.store.model.Order;
import org.robovm.store.util.Images;
import org.robovm.store.views.SwipableListItem;
import org.robovm.store.views.ViewSwipeTouchListener;

public class BasketFragment extends ListFragment {
    private Basket basket;
    private Button checkoutButton;

    private Runnable checkoutListener;

    public BasketFragment() {}

    public BasketFragment(Basket basket) {
        this.basket = basket;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Basket");
        View shoppingCartView = inflater.inflate(R.layout.basket, container, false);

        checkoutButton = (Button) shoppingCartView.findViewById(R.id.checkoutBtn);
        checkoutButton.setOnClickListener((b) -> {
            if (checkoutListener != null) {
                checkoutListener.run();
            }
        });
        shoppingCartView.setPivotY(0);
        shoppingCartView.setPivotX(container.getWidth());

        return shoppingCartView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDividerHeight(0);
        getListView().setDivider(null);
        setListAdapter(new GroceryListAdapter(view.getContext(), basket));
        if (getListAdapter().getCount() == 0) {
            checkoutButton.setVisibility(View.INVISIBLE);
        }

        basket.addOnBasketChangeListener(
                () -> checkoutButton.setVisibility(basket.size() > 0 ? View.VISIBLE : View.INVISIBLE));
    }

    public static class GroceryListAdapter extends BaseAdapter {
        private Context context;
        private Basket basket;

        public GroceryListAdapter(Context context, Basket basket) {
            this.context = context;
            this.basket = basket;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return basket.get(position).toString();
        }

        @Override
        public int getCount() {
            return basket.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Order order = basket.get(position);

            View view = convertView; // re-use an existing view, if one is available
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false);
                ViewSwipeTouchListener swipper = ((SwipableListItem) view).getSwipeListener();
                final View finalView = view;
                swipper.addEventListener(new ViewSwipeTouchListener.EventListener() {
                    @Override
                    public void onSwipeGestureBegin() {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }

                    @Override
                    public void onSwipeGestureEnd() {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }

                    @Override
                    public void onItemSwipped() {
                        // If view has already been processed, do nothing
                        if (finalView.getParent() == null) {
                            return;
                        }
                        int p = ((ListView) parent).getPositionForView(finalView);
                        Basket basket = RoboVMWebService.getInstance().getBasket();
                        basket.remove(p);
                        notifyDataSetChanged();
                    }
                });
            }

            ((TextView) view.findViewById(R.id.productTitle)).setText(order.getProduct().getName());
            ((TextView) view.findViewById(R.id.productPrice)).setText(order.getProduct().getPriceDescription());
            ((TextView) view.findViewById(R.id.productColor)).setText(order.getColor().getName());
            ((TextView) view.findViewById(R.id.productSize)).setText(order.getSize().getName());

            ImageView orderImage = (ImageView) view.findViewById(R.id.productImage);
            orderImage.setImageResource(R.drawable.product_image);

            Images.setImageFromUrlAsync(orderImage, order.getColor().getImageUrls().get(0));

            return view;
        }
    }

    public void setCheckoutListener(Runnable checkoutClickedListener) {
        this.checkoutListener = checkoutClickedListener;
    }
}
