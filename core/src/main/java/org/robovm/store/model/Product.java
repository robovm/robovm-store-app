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
package org.robovm.store.model;

import org.robovm.store.util.I18N;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private ProductType type;
    private List<ProductColor> colors;
    private List<ProductSize> sizes;

    private final Random random = new Random();
    private int imageIndex = -1;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceDescription() {
        return price < 0.01 ? I18N.getLocalizedString(I18N.Key.free) : NumberFormat.getCurrencyInstance().format(price);
    }

    public ProductType getType() {
        return type;
    }

    public List<ProductColor> getColors() {
        return colors;
    }

    public List<ProductSize> getSizes() {
        return sizes;
    }

    public List<String> getImageUrls() {
        List<String> urls = new ArrayList<>();
        if (colors != null) {
            for (ProductColor color : colors) {
                urls.addAll(color.getImageUrls());
            }
        }
        return urls;
    }

    public String getImageUrl() {
        List<String> imageUrls = getImageUrls();
        if (imageUrls == null || imageUrls.isEmpty()) {
            return "";
        }
        if (imageUrls.size() == 1) {
            return imageUrls.get(0);
        }
        if (imageIndex == -1) {
            imageIndex = random.nextInt(imageUrls.size());
        }
        return imageUrls.get(imageIndex);
    }

    @Override
    public String toString() {
        return name;
    }
}
