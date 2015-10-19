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
package org.robovm.store.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.NSLineBreakMode;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.model.Product;
import org.robovm.store.util.Colors;

public class ProductDescriptionView extends UIView {
    private static final float PADDING = 20;
    private static final float PRICE_WIDTH = 50;

    private final UILabel name;
    private final UILabel descriptionLabel;
    private final UILabel price;

    public ProductDescriptionView(Product product) {
        this();
        update(product);
    }

    public ProductDescriptionView() {
        name = new UILabel();
        name.setText("Name");
        name.setBackgroundColor(Colors.Clear);
        name.setTextColor(Colors.Gray);
        name.setFont(UIFont.getSystemFont(25));
        name.setTranslatesAutoresizingMaskIntoConstraints(false);
        name.sizeToFit();
        addSubview(name);

        descriptionLabel = new UILabel();
        descriptionLabel.setBackgroundColor(Colors.Clear);
        descriptionLabel.setTextColor(Colors.LightGray);
        descriptionLabel.setTranslatesAutoresizingMaskIntoConstraints(false);
        descriptionLabel.setFont(UIFont.getSystemFont(14));
        descriptionLabel.setLineBreakMode(NSLineBreakMode.WordWrapping);
        descriptionLabel.setNumberOfLines(0);
        addSubview(descriptionLabel);

        price = new UILabel();
        price.setBackgroundColor(Colors.Clear);
        price.setText("Price");
        price.setTextColor(Colors.Blue);
        price.setTranslatesAutoresizingMaskIntoConstraints(false);
        price.sizeToFit();
        addSubview(price);
    }

    public void update(Product product) {
        name.setText(product.getName());
        descriptionLabel.setText(product.getDescription());
        price.setText(product.getPriceDescription());
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        CGRect bounds = getBounds();
        CGRect frame = name.getFrame();

        frame.setWidth(bounds.getWidth() - (PRICE_WIDTH + PADDING * 2));
        frame.setX(PADDING);
        frame.setY(PADDING);
        name.setFrame(frame);

        frame = price.getFrame();
        frame.setY(PADDING + (name.getFrame().getHeight() - frame.getHeight()) / 2);
        frame.setX(name.getFrame().getX() + name.getFrame().getWidth() + PADDING);
        frame.setWidth(PRICE_WIDTH);
        price.setFrame(frame);

        frame = bounds;
        frame.setY(name.getFrame().getY() + name.getFrame().getHeight());
        frame.setX(PADDING);
        frame.setWidth(frame.getWidth() - PADDING * 2);
        frame.setHeight(frame.getHeight() - frame.getY());
        descriptionLabel.setFrame(frame);
    }
}
