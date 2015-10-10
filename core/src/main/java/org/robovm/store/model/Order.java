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

public class Order {
    private transient Product product;
    private transient ProductSize productSize;
    private transient ProductColor productColor;

    String id;
    String size;
    String color;

    public Order(Order order) {
        setProduct(order.getProduct());
        setSize(order.getSize());
        setColor(order.getColor());
    }

    public Order(Product product) {
        setProduct(product);
        setSize(product.getSizes().get(0));
        setColor(product.getColors().get(0));
    }

    public Order(Product product, ProductSize size, ProductColor color) {
        setProduct(product);
        setSize(size);
        setColor(color);
    }

    private void setProduct(Product product) {
        this.product = product;
        this.id = product.getId();
    }

    public Product getProduct() {
        return product;
    }

    public ProductColor getColor() {
        return productColor;
    }

    public ProductSize getSize() {
        return productSize;
    }

    public void setSize(ProductSize size) {
        this.productSize = size;
        this.size = size.getId();
    }

    public void setColor(ProductColor color) {
        this.productColor = color;
        this.color = color.getName();
    }

    @Override
    public String toString() {
        return product + " " + color + " " + size;
    }
}
