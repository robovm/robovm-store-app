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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class ImageCacheTest {
    private static final String IMAGE_URL = "https://robovm.com/wp-content/uploads/2015/03/RoboVM-logo-wide.png";

    @Before
    public void setup() {
        ImageCache.getInstance().setSaveLocation(System.getProperty("user.home") + "/StoreAppTest");
    }

    @Test
    public void shouldDownloadAndSaveImage() {
        File image = ImageCache.getInstance().downloadImage(IMAGE_URL);
        assertTrue(image.exists());
    }

    @Test
    public void shouldFallbackToDefaultImage() {
        File image = ImageCache.getInstance().downloadImage("http://www.robovm.com/not_existant.jpg");
        assertTrue(image != null && image.getName().equals("placeholder.jpg"));
    }
}
