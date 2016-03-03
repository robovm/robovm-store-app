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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.robovm.apple.coreanimation.CAAnimation;
import org.robovm.apple.coreanimation.CAAnimationCalculationMode;
import org.robovm.apple.coreanimation.CAAnimationDelegateAdapter;
import org.robovm.apple.coreanimation.CAAnimationGroup;
import org.robovm.apple.coreanimation.CABasicAnimation;
import org.robovm.apple.coreanimation.CAFillMode;
import org.robovm.apple.coreanimation.CAKeyframeAnimation;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSValue;
import org.robovm.apple.uikit.UIBezierPath;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewModel;
import org.robovm.apple.uikit.UITableViewScrollPosition;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.StoreApp;
import org.robovm.store.model.Order;
import org.robovm.store.model.Product;
import org.robovm.store.model.ProductColor;
import org.robovm.store.model.ProductSize;
import org.robovm.store.util.Action;
import org.robovm.store.util.ImageCache;
import org.robovm.store.views.BottomButtonView;
import org.robovm.store.views.CustomViewCell;
import org.robovm.store.views.JBKenBurnsView;
import org.robovm.store.views.ProductDescriptionView;
import org.robovm.store.views.SpinnerCell;
import org.robovm.store.views.StringSelectionCell;

public class ProductDetailViewController extends UITableViewController {
    private Action<Order> addedToBasket;

    private final Product currentProduct;
    private final Order order;

    private final BottomButtonView bottomView;
    private StringSelectionCell colorCell, sizeCell;
    private JBKenBurnsView imageView;
    private final UIImage tshirtIcon;

    private List<ProductColor> colorOptions;
    private List<ProductSize> sizeOptions;
    private List<String> imageUrls;

    public ProductDetailViewController(Product product) {
        this.currentProduct = product;
        this.order = new Order(product);

        setTitle(currentProduct.getName());
        loadProductData();

        getTableView().setTableFooterView(new UIView(new CGRect(0, 0, 0, BottomButtonView.HEIGHT)));

        tshirtIcon = UIImage.getImage("t-shirt");

        bottomView = new BottomButtonView();
        bottomView.setButtonText("Add to Basket");
        bottomView.setButtonTapListener((b, e) -> addToBasket());

        getView().addSubview(bottomView);
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Product Detail");
    }

    private void addToBasket() {
        UINavigationController navigation = getNavigationController();

        CGPoint center = bottomView.getButton().convertPointToView(bottomView.getButton().getImageView().getCenter(),
                navigation.getView());
        UIImageView imageView = new UIImageView(tshirtIcon);
        imageView.setCenter(center);
        imageView.setContentMode(UIViewContentMode.ScaleAspectFill);

        UIImageView backgroundView = new UIImageView(UIImage.getImage("circle"));
        backgroundView.setCenter(center);

        navigation.getView().addSubviews(backgroundView, imageView);

        UIView targetView = (UIView) getNavigationItem().getRightBarButtonItem().getKeyValueCoder().getValue("view");
        CGPoint targetPosition = new CGPoint(targetView.getCenter().getX() + targetView.getFrame().getWidth() / 3,
                targetView.getCenter().getY() + targetView.getFrame().getHeight() / 2);

        animateView(imageView, targetPosition, null);
        animateView(backgroundView, targetPosition, () -> {
            getNavigationItem().setRightBarButtonItem(StoreApp.getInstance().createBasketButton());
        });

        if (addedToBasket != null) {
            addedToBasket.invoke(new Order(order));
        }
    }

    private void animateView(UIView view, CGPoint targetPosition, Runnable completion) {
        CGSize size = view.getFrame().getSize();
        CGSize grow = new CGSize(size.getWidth() * 1.7, size.getHeight() * 1.7);
        CGSize shrink = new CGSize(size.getWidth() * .4, size.getHeight() * .4);

        CAKeyframeAnimation pathAnimation = new CAKeyframeAnimation("position");
        pathAnimation.setCalculationMode(CAAnimationCalculationMode.Paced);
        pathAnimation.setFillMode(CAFillMode.Forwards);
        pathAnimation.setRemovedOnCompletion(false);
        pathAnimation.setDuration(.5);

        UIBezierPath path = new UIBezierPath();
        path.move(view.getCenter());
        path.addQuadCurve(targetPosition, new CGPoint(view.getCenter().getX(), view.getCenter()
                .getY()));
        pathAnimation.setPath(path.getCGPath());

        CABasicAnimation growAnimation = new CABasicAnimation("bounds.size");
        growAnimation.setToValue(NSValue.valueOf(grow));
        growAnimation.setFillMode(CAFillMode.Forwards);
        growAnimation.setRemovedOnCompletion(false);
        growAnimation.setDuration(.1);

        CABasicAnimation shrinkAnimation = new CABasicAnimation("bounds.size");
        shrinkAnimation.setToValue(NSValue.valueOf(shrink));
        shrinkAnimation.setFillMode(CAFillMode.Forwards);
        shrinkAnimation.setRemovedOnCompletion(false);
        shrinkAnimation.setDuration(.4);
        shrinkAnimation.setBeginTime(.1);

        CAAnimationGroup animations = new CAAnimationGroup();
        animations.setAnimations(new NSArray<>(pathAnimation, growAnimation, shrinkAnimation));
        animations.setFillMode(CAFillMode.Forwards);
        animations.setRemovedOnCompletion(false);
        animations.setDuration(.5);
        animations.setDelegate(new CAAnimationDelegateAdapter() {
            @Override
            public void didStop(CAAnimation anim, boolean flag) {
                view.removeFromSuperview();

                if (completion != null) {
                    completion.run();
                }
            }
        });
        view.getLayer().addAnimation(animations, "movetocart");
    }

    public void loadProductData() {
        colorOptions = currentProduct.getColors();
        sizeOptions = currentProduct.getSizes();
        imageUrls = currentProduct.getImageUrls();
        Collections.shuffle(imageUrls);

        boolean loadImages = false;

        List<UIImage> images = new ArrayList<>();
        for (String url : imageUrls) {
            File file = new File(url);
            if (file.exists()) {
                images.add(new UIImage(file));
            } else {
                loadImages = true;
            }
        }

        imageView = new JBKenBurnsView(new CGRect(0, -60, UIScreen.getMainScreen().getBounds().getWidth(), 400));
        imageView.setImages(images);
        imageView.setUserInteractionEnabled(false);

        if (loadImages) {
            // Add spinner while loading data.
            getTableView().setModel(new ProductDetailPageModel(new SpinnerCell()));

            loadImages(this::fillViewController);
        } else {
            fillViewController();
        }
    }

    private void loadImages(Runnable completed) {
        new Thread(() -> {
            List<UIImage> images = new ArrayList<>();

            for (String url : imageUrls) {
                File path = ImageCache.getInstance().downloadImage(url);
                if (path != null) {
                    images.add(new UIImage(path));
                }
            }
            NSOperationQueue.getMainQueue().addOperation(() -> {
                imageView.setImages(images);
                completed.run();
            });
        }).start();
    }

    private void fillViewController() {
        ProductDescriptionView productDescriptionView = new ProductDescriptionView(currentProduct);
        productDescriptionView.setFrame(new CGRect(0, 0, 320, 120));

        UIView headerView = new UIView(new CGRect(0, 0, imageView.getFrame().getWidth(), imageView.getFrame()
                .getY() + imageView.getFrame().getHeight()));
        headerView.addSubview(imageView);
        getTableView().setTableHeaderView(headerView);

        imageView.animate();

        List<UITableViewCell> tableItems = new ArrayList<>();
        tableItems.add(new CustomViewCell(productDescriptionView));
        tableItems.addAll(getOptionCells());

        getTableView().setModel(new ProductDetailPageModel(tableItems));
        getTableView().reloadData();
    }

    private List<UITableViewCell> getOptionCells() {
        List<UITableViewCell> cells = new ArrayList<>();

        sizeCell = new StringSelectionCell(getView());
        sizeCell.setText("Size");
        List<String> sizeItems = new ArrayList<>();
        for (ProductSize size : sizeOptions) {
            sizeItems.add(size.getName());
        }
        sizeCell.setItems(sizeItems);
        sizeCell.setDetailText(sizeOptions.get(sizeCell.getSelectedIndex()).getName());
        sizeCell.setSelectionListener((s) -> {
            ProductSize size = sizeOptions.get(sizeCell.getSelectedIndex());
            order.setSize(size);
        });
        cells.add(sizeCell);

        colorCell = new StringSelectionCell(getView());
        colorCell.setText("Color");
        List<String> colorItems = new ArrayList<>();
        for (ProductColor color : colorOptions) {
            colorItems.add(color.getName());
        }
        colorCell.setItems(colorItems);
        colorCell.setDetailText(colorOptions.get(colorCell.getSelectedIndex()).getName());
        colorCell.setSelectionListener((s) -> {
            ProductColor color = colorOptions.get(colorCell.getSelectedIndex());
            order.setColor(color);
        });
        cells.add(colorCell);

        return cells;
    }

    public void setAddToBasketListener(Action<Order> listener) {
        this.addedToBasket = listener;
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        getNavigationItem().setRightBarButtonItem(StoreApp.getInstance().createBasketButton());
        imageView.animate();

        NSIndexPath bottomRow = NSIndexPath.row(getTableView().getNumberOfRowsInSection(0) - 1, 0);
        getTableView().scrollToRow(bottomRow, UITableViewScrollPosition.Top, false);
    }

    @Override
    public void viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews();

        CGRect bounds = getView().getBounds();
        bounds.setY(bounds.getY() + bounds.getHeight() - BottomButtonView.HEIGHT);
        bounds.setHeight(BottomButtonView.HEIGHT);
        bottomView.setFrame(bounds);
    }

    static class ProductDetailPageModel extends UITableViewModel {
        private final List<UITableViewCell> tableItems;

        public ProductDetailPageModel(UITableViewCell item) {
            this.tableItems = new ArrayList<>();
            this.tableItems.add(item);
        }

        public ProductDetailPageModel(List<UITableViewCell> items) {
            this.tableItems = items;
        }

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            return tableItems.size();
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            return tableItems.get(indexPath.getRow());
        }

        @Override
        public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
            if (tableItems.size() == 1 && tableItems.get(0) instanceof SpinnerCell) {
                return tableView.getFrame().getHeight();
            }
            return tableItems.get(indexPath.getRow()).getFrame().getHeight();
        }

        @Override
        public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
            if (tableItems.get(indexPath.getRow()) instanceof StringSelectionCell) {
                ((StringSelectionCell) tableItems.get(indexPath.getRow())).tap();
            }
            tableView.deselectRow(indexPath, true);
        }
    }
}
