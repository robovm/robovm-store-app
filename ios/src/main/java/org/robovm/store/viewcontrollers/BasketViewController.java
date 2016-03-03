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
import java.text.NumberFormat;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellEditingStyle;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewModel;
import org.robovm.apple.uikit.UITableViewRowAnimation;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.model.Basket;
import org.robovm.store.model.Order;
import org.robovm.store.util.Colors;
import org.robovm.store.util.ImageCache;
import org.robovm.store.views.BottomButtonView;
import org.robovm.store.views.EmptyBasketView;

public class BasketViewController extends UITableViewController {
    private Basket basket;
    private EmptyBasketView emptyCartImageView;
    private BottomButtonView bottomView;
    private final UILabel totalAmount;

    private Runnable checkout;

    public BasketViewController(Basket basket) {
        setTitle("Your Basket");
        // This hides the back button text when you leave this View Controller
        getNavigationItem().setBackBarButtonItem(new UIBarButtonItem("", UIBarButtonItemStyle.Plain));

        UITableView tableView = getTableView();
        tableView.setModel(new BasketTableModel(this.basket = basket, this::checkEmpty));
        tableView.setSeparatorStyle(UITableViewCellSeparatorStyle.None);
        tableView.setRowHeight(75);
        tableView.setTableFooterView(new UIView(new CGRect(0, 0, 0, BottomButtonView.HEIGHT)));

        getView().addSubview(bottomView = new BottomButtonView("Checkout", (b, e) -> {
            if (checkout != null) {
                checkout.run();
            }
        }));
        checkEmpty(false);
        totalAmount = new UILabel();
        totalAmount.setText("$1,000");
        totalAmount.setTextColor(Colors.White);
        totalAmount.setTextAlignment(NSTextAlignment.Center);
        totalAmount.setFont(UIFont.getBoldSystemFont(17));
        totalAmount.sizeToFit();
        getNavigationItem().setRightBarButtonItem(new UIBarButtonItem(totalAmount));
        updateTotals();
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Basket");
    }

    public void updateTotals() {
        if (basket.size() == 0) {
            totalAmount.setText("");
        } else {
            double total = 0;
            for (Order order : basket) {
                total += order.getProduct().getPrice();
            }
            totalAmount.setText(NumberFormat.getCurrencyInstance().format(total));
        }
    }

    @Override
    public void viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews();

        CGRect bounds = getView().getBounds();
        bounds.setY(bounds.getY() + bounds.getHeight() - BottomButtonView.HEIGHT);
        bounds.setHeight(BottomButtonView.HEIGHT);
        bottomView.setFrame(bounds);

        if (emptyCartImageView != null) {
            emptyCartImageView.setFrame(getView().getBounds());
        }
    }

    private void checkEmpty() {
        updateTotals();
        checkEmpty(true);
    }

    private void checkEmpty(boolean animate) {
        if (basket.size() == 0) {
            emptyCartImageView = new EmptyBasketView();
            emptyCartImageView.setAlpha(animate ? 0 : 1);
            getView().addSubview(emptyCartImageView);
            getView().bringSubviewToFront(emptyCartImageView);

            if (animate) {
                UIView.animate(.25, () -> emptyCartImageView.setAlpha(1));
            }
        } else if (emptyCartImageView != null) {
            emptyCartImageView.removeFromSuperview();
            emptyCartImageView = null;
        }
    }

    public void setCheckoutListener(Runnable listener) {
        this.checkout = listener;
    }

    static class BasketTableModel extends UITableViewModel {
        private final Runnable rowDeleted;

        private final Basket basket;

        public BasketTableModel(Basket basket, Runnable rowDeleted) {
            this.basket = basket;
            this.rowDeleted = rowDeleted;
        }

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            return basket.size();
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            ProductCell cell = (ProductCell) tableView.dequeueReusableCell(ProductCell.IDENTIFIER);
            if (cell == null) {
                cell = new ProductCell();
            }
            cell.update(basket.get(indexPath.getRow()));
            return cell;
        }

        @Override
        public UITableViewCellEditingStyle getEditingStyleForRow(UITableView tableView,
                NSIndexPath indexPath) {
            return UITableViewCellEditingStyle.Delete;
        }

        @Override
        public void commitEditingStyleForRow(UITableView tableView, UITableViewCellEditingStyle editingStyle,
                NSIndexPath indexPath) {
            if (editingStyle == UITableViewCellEditingStyle.Delete) {
                basket.remove(indexPath.getRow());
                tableView.deleteRow(indexPath, UITableViewRowAnimation.Fade);
                if (rowDeleted != null) {
                    rowDeleted.run();
                }
            }
        }

        static class ProductCell extends UITableViewCell {
            public static final String IDENTIFIER = "productCell";
            private static final CGSize IMAGE_SIZE = new CGSize(55, 55);
            private static final float LEFT_PADDING = 15f;
            private static final float TOP_PADDING = 5f;

            private final UILabel nameLabel;
            private final UILabel sizeLabel;
            private final UILabel colorLabel;
            private final UILabel priceLabel;
            private final UIView lineView;

            public ProductCell() {
                super(UITableViewCellStyle.Default, IDENTIFIER);

                setSelectionStyle(UITableViewCellSelectionStyle.None);
                getContentView().setBackgroundColor(Colors.Clear);

                UIImageView imageView = getImageView();
                imageView.setImage(UIImage.getImage("shirt_image"));
                imageView.setContentMode(UIViewContentMode.ScaleAspectFill);
                imageView.setFrame(new CGRect(CGPoint.Zero(), IMAGE_SIZE));
                imageView.setBackgroundColor(Colors.Clear);
                imageView.getLayer().setCornerRadius(5);
                imageView.getLayer().setMasksToBounds(true);

                nameLabel = new UILabel();
                nameLabel.setText("Name");
                nameLabel.setFont(UIFont.getBoldSystemFont(17));
                nameLabel.setBackgroundColor(Colors.Clear);
                nameLabel.sizeToFit();
                getContentView().addSubview(nameLabel);

                sizeLabel = new UILabel();
                sizeLabel.setText("Size");
                sizeLabel.setFont(UIFont.getBoldSystemFont(12));
                sizeLabel.setBackgroundColor(Colors.Clear);
                sizeLabel.setTextColor(Colors.LightGray);
                sizeLabel.sizeToFit();
                getContentView().addSubview(sizeLabel);

                colorLabel = new UILabel();
                colorLabel.setText("Color");
                colorLabel.setFont(UIFont.getBoldSystemFont(12));
                colorLabel.setBackgroundColor(Colors.Clear);
                colorLabel.setTextColor(Colors.LightGray);
                colorLabel.sizeToFit();
                getContentView().addSubview(colorLabel);

                priceLabel = new UILabel();
                priceLabel.setText("Price");
                priceLabel.setFont(UIFont.getBoldSystemFont(15));
                priceLabel.setBackgroundColor(Colors.Clear);
                priceLabel.setTextAlignment(NSTextAlignment.Right);
                priceLabel.setTextColor(Colors.Blue);
                priceLabel.sizeToFit();

                UIView accessoryView = new UIView(new CGRect(0, 0, priceLabel.getFrame().getWidth() + 10, 54));
                accessoryView.setBackgroundColor(Colors.Clear);
                accessoryView.addSubview(priceLabel);
                setAccessoryView(accessoryView);

                lineView = new UIView();
                lineView.setBackgroundColor(Colors.LightGray);
                getContentView().addSubview(lineView);
            }

            @Override
            public void layoutSubviews() {
                super.layoutSubviews();

                CGRect bounds = getContentView().getBounds();
                double midY = bounds.getMidY();

                CGPoint center = new CGPoint(IMAGE_SIZE.getWidth() / 2 + LEFT_PADDING, midY);
                UIImageView imageView = getImageView();
                imageView.setFrame(new CGRect(CGPoint.Zero(), IMAGE_SIZE));
                imageView.setCenter(center);

                double x = imageView.getFrame().getX() + imageView.getFrame().getWidth() + LEFT_PADDING;
                double y = imageView.getFrame().getY();
                double labelWidth = bounds.getWidth() - (x + LEFT_PADDING * 2);

                nameLabel.setFrame(new CGRect(x, y, labelWidth, nameLabel.getFrame().getHeight()));
                y = nameLabel.getFrame().getY() + nameLabel.getFrame().getHeight();

                sizeLabel.setFrame(new CGRect(x, y, labelWidth, sizeLabel.getFrame().getHeight()));
                y = sizeLabel.getFrame().getY() + sizeLabel.getFrame().getHeight();

                colorLabel.setFrame(new CGRect(x, y, labelWidth, colorLabel.getFrame().getHeight()));
                y = colorLabel.getFrame().getY() + colorLabel.getFrame().getHeight() + TOP_PADDING;
                lineView.setFrame(new CGRect(0, bounds.getHeight() - .5, UIScreen.getMainScreen().getBounds()
                        .getWidth(), .5));
            }

            public void update(Order order) {
                nameLabel.setText(order.getProduct().getName());
                sizeLabel.setText(order.getSize().getName());
                colorLabel.setText(order.getColor().getName());
                priceLabel.setText(order.getProduct().getPriceDescription());

                String imageUrl = order.getColor().getImageUrls().get(0);
                File image = ImageCache.getInstance().getImage(imageUrl);
                if (image != null) {
                    getImageView().setImage(new UIImage(image));
                } else {
                    // Put default before doing the web request;
                    getImageView().setImage(UIImage.getImage("shirt_image"));
                    ImageCache.getInstance().downloadImage(imageUrl, (file) -> {
                        getImageView().setImage(new UIImage(file));
                    });
                }
            }
        }
    }
}
