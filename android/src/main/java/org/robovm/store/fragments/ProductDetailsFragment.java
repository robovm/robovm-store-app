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

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.robovm.store.R;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.model.*;
import org.robovm.store.util.*;
import org.robovm.store.views.BadgeDrawable;
import org.robovm.store.views.KenBurnsDrawable;
import org.robovm.store.views.SlidingLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ProductDetailsFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final float ENLARGE_RATIO = 1.1f;

    private Action<Order> addToBasketListener;

    private Product currentProduct;
    private Order order;

    private ImageView productImage;

    private final Random random = new Random();
    private int currentIndex;
    private boolean shouldAnimatePop;
    private BadgeDrawable basketBadge;
    private List<String> images = new ArrayList<>();
    private boolean cached;
    private int slidingDelta;
    private Spinner sizeSpinner;
    private Spinner colorSpinner;

    private KenBurnsDrawable productDrawable;
    private ValueAnimator kenBurnsMovement;
    private ValueAnimator kenBurnsAlpha;

    public ProductDetailsFragment() {}

    public ProductDetailsFragment(Product product, int slidingDelta) {
        this.currentProduct = product;
        this.slidingDelta = slidingDelta;
        this.order = new Order(product);

        images = product.getImageUrls();
        Collections.shuffle(images);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Product Detail");
        return inflater.inflate(R.layout.product_detail, null, true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productImage = (ImageView) view.findViewById(R.id.productImage);
        sizeSpinner = (Spinner) view.findViewById(R.id.productSize);
        colorSpinner = (Spinner) view.findViewById(R.id.productColor);

        Button addToBasket = (Button) view.findViewById(R.id.addToBasket);
        addToBasket.setOnClickListener((button) -> {
            order.setSize(currentProduct.getSizes().get(sizeSpinner.getSelectedItemPosition()));
            order.setColor(currentProduct.getColors().get(colorSpinner.getSelectedItemPosition()));
            shouldAnimatePop = true;
            getActivity().getFragmentManager().popBackStack();
            if (addToBasketListener != null) {
                addToBasketListener.invoke(new Order(order));
            }
        });

        ((TextView) view.findViewById(R.id.productTitle)).setText(currentProduct.getName());
        ((TextView) view.findViewById(R.id.productPrice)).setText(currentProduct.getPriceDescription());
        ((TextView) view.findViewById(R.id.productDescription)).setText(currentProduct.getDescription());

        ((SlidingLayout) view).setInitialMainViewDelta(slidingDelta);

        loadOptions();
    }

    private void loadOptions() {
        ArrayAdapter<ProductSize> sizeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, currentProduct.getSizes());
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sizeSpinner.setAdapter(sizeAdapter);

        ArrayAdapter<ProductColor> colorAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, currentProduct.getColors());
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        colorSpinner.setAdapter(colorAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        animateImages();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (kenBurnsAlpha != null) {
            kenBurnsAlpha.cancel();
        }
        if (kenBurnsMovement != null) {
            kenBurnsMovement.cancel();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem cartItem = menu.findItem(R.id.cart_menu_item);
        cartItem.setIcon(basketBadge = new BadgeDrawable(cartItem.getIcon()));

        Basket basket = RoboVMWebService.getInstance().getBasket();
        basketBadge.setCount(basket.size());
        basket.addOnBasketChangeListener(() -> basketBadge.setCountAnimated(basket.size()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (!enter && shouldAnimatePop) {
            return AnimatorInflater.loadAnimator(getView().getContext(), R.anim.add_to_basket_in);
        }
        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    private void animateImages() {
        if (images.size() < 1) {
            return;
        }
        if (images.size() == 1) {
            Images.setImageFromUrlAsync(productImage, images.get(0));
            return;
        }
        productImage.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        productImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        new Thread(() -> {
            Bitmap img1 = Images.fromUrl(images.get(0));
            Bitmap img2 = Images.fromUrl(images.get(1));

            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    productDrawable = new KenBurnsDrawable(Colors.Green);
                    productDrawable.setFirstBitmap(img1);
                    productDrawable.setSecondBitmap(img2);
                    productImage.setImageDrawable(productDrawable);
                    currentIndex++;

                    // Check for null bitmaps due to decode errors:
                    if (productDrawable.getFirstBitmap() != null) {
                        float resizeRatio = -1;
                        float widthDiff = -1;
                        float heightDiff = -1;
                        float zoomInX = -1;
                        float zoomInY = -1;
                        float moveX = -1;
                        float moveY = -1;

                        float frameWidth = productImage.getWidth();
                        float frameHeight = productImage.getHeight();

                        float imageWidth = productDrawable.getFirstBitmap().getWidth();
                        float imageHeight = productDrawable.getFirstBitmap().getHeight();

                        // Wider than screen
                        if (imageWidth > frameWidth) {
                            widthDiff = imageWidth - frameWidth;

                            // Higher than screen
                            if (imageHeight > frameHeight) {
                                heightDiff = imageHeight - frameHeight;

                                if (widthDiff > heightDiff)
                                    resizeRatio = frameHeight / imageHeight;
                                else
                                    resizeRatio = frameWidth / imageWidth;

                                // No higher than screen [OK]
                            } else {
                                heightDiff = frameHeight - imageHeight;

                                if (widthDiff > heightDiff)
                                    resizeRatio = frameWidth / imageWidth;
                                else
                                    resizeRatio = frameHeight / imageHeight;
                            }
                            // No wider than screen
                        } else {
                            widthDiff = frameWidth - imageWidth;

                            // Higher than screen [OK]
                            if (imageHeight > frameHeight) {
                                heightDiff = imageHeight - frameHeight;

                                if (widthDiff > heightDiff)
                                    resizeRatio = imageHeight / frameHeight;
                                else
                                    resizeRatio = frameWidth / imageWidth;

                                // No higher than screen [OK]
                            } else {
                                heightDiff = frameHeight - imageHeight;

                                if (widthDiff > heightDiff)
                                    resizeRatio = frameWidth / imageWidth;
                                else
                                    resizeRatio = frameHeight / imageHeight;
                            }
                        }

                        // Resize the image.
                        float optimusWidth = (imageWidth * resizeRatio) * ENLARGE_RATIO;
                        float optimusHeight = (imageHeight * resizeRatio) * ENLARGE_RATIO;

                        float originX = (frameWidth - optimusWidth) / 2;
                        float originY = 0;

                        float maxMoveX = Math.min(optimusWidth - frameWidth, 50f);
                        float maxMoveY = Math.min(optimusHeight - frameHeight, 50f) * 2f / 3;

                        float rotation = random.nextInt(9) / 100f;

                        switch (random.nextInt(3)) {
                        case 0:
                            zoomInX = 1.25f;
                            zoomInY = 1.25f;
                            moveX = -maxMoveX;
                            moveY = -maxMoveY;
                            break;
                        case 1:
                            zoomInX = 1.1f;
                            zoomInY = 1.1f;
                            moveX = -maxMoveX;
                            moveY = maxMoveY;
                            originY = -moveY * zoomInY * 1.1f;
                            break;
                        case 2:
                            zoomInX = 1.2f;
                            zoomInY = 1.2f;
                            moveX = 0;
                            moveY = -maxMoveY;
                            break;
                        default:
                            zoomInX = 1.2f;
                            zoomInY = 1.2f;
                            moveX = 0;
                            moveY = maxMoveY;
                            originY = -moveY * zoomInY * 1.1f;
                            break;
                        }

                        MatrixEvaluator evaluator = new MatrixEvaluator();
                        Matrix startMatrix = new Matrix();
                        startMatrix.setTranslate(originX, originY);
                        startMatrix
                                .postScale(resizeRatio * ENLARGE_RATIO, resizeRatio * ENLARGE_RATIO, originX, originY);

                        Matrix finalMatrix = new Matrix();
                        finalMatrix.setTranslate(originX + moveX, originY + moveY);
                        finalMatrix
                                .postScale(resizeRatio * ENLARGE_RATIO * zoomInX, resizeRatio * ENLARGE_RATIO * zoomInY,
                                        originX, originY);
                        finalMatrix.postRotate(rotation);

                        kenBurnsMovement = ValueAnimator.ofObject(evaluator, startMatrix, finalMatrix);
                        kenBurnsMovement.addUpdateListener(
                                (animator) -> productDrawable.setMatrix((Matrix) animator.getAnimatedValue()));
                        kenBurnsMovement.setDuration(14000);
                        kenBurnsMovement.setRepeatMode(ValueAnimator.REVERSE);
                        kenBurnsMovement.setRepeatCount(ValueAnimator.INFINITE);
                        kenBurnsMovement.start();

                        kenBurnsAlpha = ObjectAnimator.ofInt(productDrawable, "alpha", 0, 0, 0, 255, 255, 255);
                        kenBurnsAlpha.setDuration(kenBurnsMovement.getDuration());
                        kenBurnsAlpha.setRepeatMode(ValueAnimator.REVERSE);
                        kenBurnsAlpha.setRepeatCount(ValueAnimator.INFINITE);
                        kenBurnsAlpha.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {}

                            @Override
                            public void onAnimationEnd(Animator animation) {}

                            @Override
                            public void onAnimationCancel(Animator animation) {}

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                nextImage();
                            }
                        });
                        kenBurnsAlpha.start();
                    }
                });
            }
        }).start();
    }

    private void nextImage() {
        currentIndex = (currentIndex + 1) % images.size();
        String image = images.get(currentIndex);
        Images.setImageFromUrlAsync(productDrawable, image);
        precacheNextImage();
    }

    private void precacheNextImage() {
        if (currentIndex + 1 >= images.size()) {
            cached = true;
        }
        if (cached) {
            return;
        }
        int next = currentIndex + 1;
        String image = images.get(next);
        ImageCache.getInstance().downloadImage(image, (f) -> {});
    }

    public void setAddToBasketListener(Action<Order> listener) {
        this.addToBasketListener = listener;
    }
}
