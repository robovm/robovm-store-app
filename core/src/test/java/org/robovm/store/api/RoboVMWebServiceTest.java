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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.robovm.store.api.RoboVMWebService.RoboVMAPI;
import org.robovm.store.model.Basket;
import org.robovm.store.model.Order;
import org.robovm.store.model.Product;
import org.robovm.store.model.ProductColor;
import org.robovm.store.model.ProductSize;
import org.robovm.store.model.ProductType;
import org.robovm.store.model.User;

import retrofit.Call;
import retrofit.Response;

public class RoboVMWebServiceTest {
    private RoboVMAPI api;

    @Before
    public void setup() {
        api = RoboVMWebService.getInstance().setup(true).getApi();
    }

    @Test
    public void apiAuthShouldReturnSuccessAndNotNullAuthToken() throws IOException {
        Call<AuthResponse> call = api.auth(new AuthRequest("dominik@robovm.com", "qwerty"));

        Response<AuthResponse> r = call.execute();
        assertTrue(r.isSuccess());
        AuthResponse response = r.body();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getAuthToken());
    }

    @Test
    public void apiProductsShouldReturnValidProducts() throws IOException {
        Call<ProductsResponse> call = api.products();

        ProductsResponse response = call.execute().body();
        assertTrue(response.isSuccess());

        List<Product> products = response.getProducts();
        assertNotNull(products);
        assertEquals(products.size(), 2);

        Product menJavaTShirt = products.get(0);
        assertEquals(menJavaTShirt.getId(), "MenJavaTShirt");
        assertEquals(menJavaTShirt.getName(), "Men's Java T-shirt");
        assertEquals(menJavaTShirt.getType(), ProductType.TShirt);

        List<ProductColor> colors = menJavaTShirt.getColors();
        assertEquals(colors.size(), 2);
        assertEquals(colors.get(1).getName(), "Navy");

        List<ProductSize> sizes = menJavaTShirt.getSizes();
        assertEquals(sizes.size(), 5);
        assertEquals(sizes.get(4).getId(), "xxl");
    }

    @Test
    public void apiOrderShouldReturnAuthError() throws IOException {
        Call<APIResponse> call = api.order(new OrderRequest(new AuthToken(""), new User(), null));

        Response<APIResponse> r = call.execute();
        assertEquals(r.code(), 403);
        assertFalse(r.isSuccess());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void apiOrderShouldReturnValidationErrors() throws IOException {
        Call<AuthResponse> authCall = api.auth(new AuthRequest("dominik@robovm.com", "qwerty"));
        AuthResponse authResponse = authCall.execute().body();
        String authToken = authResponse.getAuthToken();

        Call<APIResponse> orderCall = api.order(new OrderRequest(new AuthToken(authToken), new User(), null));
        APIResponse orderResponse = orderCall.execute().body();
        assertFalse(orderResponse.isSuccess());

        List<ValidationError> errors = orderResponse.getErrors();
        assertNotNull(errors);
        assertEquals(errors.size(), 8);
        assertThat(errors,
                hasItems(hasProperty("field", is("firstName")), hasProperty("field", is("lastName")),
                        hasProperty("field", is("address1")), hasProperty("field", is("city")),
                        hasProperty("field", is("zipCode")), hasProperty("field", is("phone")),
                        hasProperty("field", is("country")), hasProperty("message", is("No products in order"))));
    }

    @Test
    public void apiOrderShouldReturnSuccessOrNoMoreShirts() throws IOException {
        Call<ProductsResponse> call = api.products();

        ProductsResponse response = call.execute().body();
        List<Product> products = response.getProducts();
        assertNotNull(products);

        Call<AuthResponse> authCall = api.auth(new AuthRequest("dominik@robovm.com", "qwerty"));
        AuthResponse authResponse = authCall.execute().body();
        String authToken = authResponse.getAuthToken();

        User user = new User();
        user.setFirstName("Clark");
        user.setLastName("Kent");
        user.setAddress1("344 Clinton St.");
        user.setAddress2("Apt. #3B");
        user.setCity("Metropolis");
        user.setZipCode("62960");
        user.setPhone("+12015612823");
        user.setCountry("US");

        Basket basket = new Basket();
        Product product = products.get(0);
        basket.add(new Order(product, product.getSizes().get(0), product.getColors().get(0)));

        Call<APIResponse> orderCall = api.order(new OrderRequest(new AuthToken(authToken), user, basket));
        APIResponse orderResponse = orderCall.execute().body();
        assertTrue(orderResponse.isSuccess()
                || (orderResponse.getErrors().size() == 1 && orderResponse.getErrors().get(0).getMessage()
                        .equals("No more t-shirts for you!")));
    }
}
