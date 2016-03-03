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

import java.util.List;

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewModel;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.StoreApp;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.model.Product;
import org.robovm.store.util.Action;
import org.robovm.store.util.Colors;
import org.robovm.store.views.SpinnerCell;
import org.robovm.store.views.TopAlignedImageView;

public class ProductListViewController extends UITableViewController {
    private static final int PRODUCT_CELL_ROW_HEIGHT = 300;

    private ProductListViewModel model;

    public ProductListViewController() {
        setTitle("RoboVM Store");

        // Hide the back button text when you leave this View Controller.
        getNavigationItem().setBackBarButtonItem(new UIBarButtonItem("", UIBarButtonItemStyle.Plain));
        UITableView tableView = getTableView();
        tableView.setSeparatorStyle(UITableViewCellSeparatorStyle.None);
        tableView.setRowHeight(PRODUCT_CELL_ROW_HEIGHT);
        tableView.setModel(model = new ProductListViewModel());

        getData();
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Product List");
    }

    private void getData() {
        RoboVMWebService.getInstance().getProducts((products) -> {
            model.setProducts(products);
            RoboVMWebService.getInstance().preloadProductImages();
            getTableView().reloadData();
        });
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        getNavigationItem().setRightBarButtonItem(StoreApp.getInstance().createBasketButton());
    }

    static class ProductListViewModel extends UITableViewModel {
        private Action<Product> productSelected;
        private List<Product> products;

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public void setProductSelectedListener(Action<Product> listener) {
            this.productSelected = listener;
        }

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            return products == null ? 1 : products.size();
        }

        @Override
        public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
            if (products == null) {
                return;
            }
            if (productSelected != null) {
                productSelected.invoke(products.get(indexPath.getRow()));
            }
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            if (products == null) {
                return new SpinnerCell();
            }

            ProductListCell cell = (ProductListCell) tableView.dequeueReusableCell(ProductListCell.IDENTIFIER);
            if (cell == null) {
                cell = new ProductListCell();
            }
            cell.setProduct(products.get(indexPath.getRow()));
            return cell;
        }
    }

    static class ProductListCell extends UITableViewCell {
        public static final String IDENTIFIER = "ProductListCell";
        private static final CGSize PRICE_LABEL_PADDING = new CGSize(16, 6);

        private Product product;
        private final TopAlignedImageView imageView;
        private final UILabel nameLabel, priceLabel;

        public ProductListCell() {
            setSelectionStyle(UITableViewCellSelectionStyle.None);
            getContentView().setBackgroundColor(Colors.LightGray);

            imageView = new TopAlignedImageView();
            imageView.setClipsToBounds(true);

            nameLabel = new UILabel();
            nameLabel.setTextColor(Colors.White);
            nameLabel.setTextAlignment(NSTextAlignment.Left);
            nameLabel.setFont(UIFont.getFont("HelveticaNeue-Light", 22));

            CALayer nameLabelLayer = nameLabel.getLayer();
            nameLabelLayer.setShadowRadius(3);
            nameLabelLayer.setShadowColor(Colors.Black.getCGColor());
            nameLabelLayer.setShadowOffset(new CGSize(0, 1));
            nameLabelLayer.setShadowOpacity(.5f);

            priceLabel = new UILabel();
            priceLabel.setAlpha(0.95);
            priceLabel.setTextColor(Colors.White);
            priceLabel.setBackgroundColor(Colors.Green);
            priceLabel.setTextAlignment(NSTextAlignment.Center);
            priceLabel.setFont(UIFont.getFont("HelveticeNeue", 16));
            priceLabel.setShadowColor(Colors.LightGray);
            priceLabel.setShadowOffset(new CGSize(.5, .5));

            CALayer priceLabelLayer = priceLabel.getLayer();
            priceLabelLayer.setCornerRadius(3);

            getContentView().addSubviews(imageView, nameLabel, priceLabel);
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;

            nameLabel.setText(product.getName());
            priceLabel.setText(product.getPriceDescription());
            updateImage();
        }

        public void updateImage() {
            String url = product.getImageUrl();
            imageView.loadUrl(url);
        }

        @Override
        public void layoutSubviews() {
            super.layoutSubviews();

            CGRect bounds = getContentView().getBounds();

            imageView.setFrame(bounds);
            nameLabel.setFrame(new CGRect(
                    bounds.getX() + 12,
                    bounds.getY() + bounds.getHeight() - 58,
                    bounds.getWidth(),
                    55));

            CGSize priceSize = NSString.getSize(product.getPriceDescription(),
                    new NSAttributedStringAttributes().setFont(priceLabel.getFont()));
            priceLabel.setFrame(new CGRect(
                    bounds.getWidth() - priceSize.getWidth() - 2 * PRICE_LABEL_PADDING.getWidth() - 12,
                    bounds.getY() + bounds.getHeight() - priceSize.getHeight() - 2 * PRICE_LABEL_PADDING.getHeight()
                            - 14,
                    priceSize.getWidth() + 2 * PRICE_LABEL_PADDING.getWidth(),
                    priceSize.getHeight() + 2 * PRICE_LABEL_PADDING.getHeight()));
        }
    }

    public void setProductSelectionListener(Action<Product> listener) {
        model.setProductSelectedListener(listener);
    }
}
