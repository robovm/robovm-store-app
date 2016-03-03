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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import org.robovm.store.R;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.model.Basket;
import org.robovm.store.model.Product;
import org.robovm.store.util.Action2;
import org.robovm.store.util.Images;
import org.robovm.store.views.BadgeDrawable;

import java.util.List;

public class ProductListFragment extends ListFragment {
    private Action2<Product, Integer> productSelectionListener;
    private BadgeDrawable basketBadge;
    private int badgeCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Product List");
        return inflater.inflate(R.layout.robovm_list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDrawSelectorOnTop(true);
        getListView().setSelector(new ColorDrawable(Color.parseColor("#30ffffff")));
        if (getListAdapter() == null) {
            setListAdapter(new ProductListViewAdapter(view.getContext()));
            getData();
        }
    }

    private void getData() {
        ProductListViewAdapter adapter = (ProductListViewAdapter) getListAdapter();
        RoboVMWebService.getInstance().getProducts((products) -> {
            adapter.setProducts(products);
            RoboVMWebService.getInstance().preloadProductImages();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ProductListViewAdapter adapter = (ProductListViewAdapter) getListAdapter();
        if (productSelectionListener != null) {
            productSelectionListener.invoke(adapter.products.get(position), v.getTop());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem cartItem = menu.findItem(R.id.cart_menu_item);
        cartItem.setIcon(basketBadge = new BadgeDrawable(cartItem.getIcon()));

        Basket basket = RoboVMWebService.getInstance().getBasket();
        if (badgeCount != basket.size()) {
            basketBadge.setCountAnimated(basket.size());
        } else {
            basketBadge.setCount(basket.size());
        }
        badgeCount = basket.size();
        basket.addOnBasketChangeListener(() -> {
            basketBadge.setCountAnimated(basket.size());
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private static class ProductListViewAdapter extends BaseAdapter {
        private Context context;
        private DecelerateInterpolator appearInterpolator = new DecelerateInterpolator();

        private List<Product> products;
        private long newItems;

        public ProductListViewAdapter(Context context) {
            this.context = context;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        @Override
        public int getCount() {
            return products == null ? 0 : products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position).toString();
        }

        @Override
        public long getItemId(int position) {
            return products.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.product_list_item, parent, false);
                convertView.setId(0x60000000);
            }
            convertView.setId(convertView.getId() + 1);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.productImage);
            TextView nameLabel = (TextView) convertView.findViewById(R.id.productTitle);
            TextView priceLabel = (TextView) convertView.findViewById(R.id.productPrice);
            ProgressBar progressView = (ProgressBar) convertView.findViewById(R.id.productImageSpinner);

            Product product = products.get(position);
            nameLabel.setText(product.getName());
            priceLabel.setText(product.getPriceDescription());

            loadProductImage(convertView, progressView, imageView, product);

            if (((newItems >> position) & 1) == 0) {
                newItems |= 1L << position;
                float density = context.getResources().getDisplayMetrics().density;
                convertView.setTranslationY(60 * density);
                convertView.setRotationX(12);
                convertView.setScaleX(1.1f);
                convertView.setPivotY(180 * density);
                convertView.setPivotX(parent.getWidth() / 2);
                convertView.animate().translationY(0).rotationX(0).scaleX(1).setDuration(450)
                        .setInterpolator(appearInterpolator).start();
            }

            return convertView;
        }

        private void loadProductImage(View mainView, ProgressBar progressView, ImageView imageView, Product product) {
            progressView.setVisibility(View.VISIBLE);
            imageView.setImageResource(android.R.color.transparent);
            Images.setImageFromUrlAsync(imageView, product.getImageUrl(), () -> {
                progressView.setVisibility(View.INVISIBLE);
            });
        }
    }

    public void setProductSelectionListener(Action2<Product, Integer> listener) {
        this.productSelectionListener = listener;
    }
}
