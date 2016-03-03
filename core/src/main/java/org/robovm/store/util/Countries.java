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
package org.robovm.store.util;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.robovm.store.model.Country;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Countries {
    private static Country[] countries;

    public static Country[] getCountries() {
        if (countries == null) {
            readCountries();
        }
        return countries;
    }

    private static void readCountries() {
        try {
            Gson gson = new Gson();
            countries = gson.fromJson(
                    IOUtils.toString(Countries.class.getResourceAsStream("/countries.json"),
                            Charset.defaultCharset()), Country[].class);
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Country getCountryForCode(String code) {
        if (countries == null) {
            return null;
        }
        for (Country country : countries) {
            if (country.getCode().equals(code)) {
                return country;
            }
        }
        return null;
    }

    public static Country getCountryForName(String name) {
        if (countries == null) {
            return null;
        }
        for (Country country : countries) {
            if (country.getName().equals(name)) {
                return country;
            }
        }
        return null;
    }
}
