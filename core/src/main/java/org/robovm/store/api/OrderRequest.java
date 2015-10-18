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
package org.robovm.store.api;

import java.util.List;

import org.robovm.store.model.Basket;
import org.robovm.store.model.Order;
import org.robovm.store.model.User;

public class OrderRequest {
    final String authToken;
    final String firstName;
    final String lastName;
    final String address1;
    final String address2;
    final String zipCode;
    final String city;
    final String state;
    final String phone;
    final String country;
    final List<Order> products;

    public OrderRequest(AuthToken authToken, User user, Basket basket) {
        this.authToken = authToken.getTokenString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.address1 = user.getAddress1();
        this.address2 = user.getAddress2();
        this.zipCode = user.getZipCode();
        this.city = user.getCity();
        this.state = user.getState();
        this.phone = user.getPhone();
        this.country = user.getCountry();
        this.products = basket.getOrders();
    }
}
