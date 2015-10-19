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

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.robovm.store.api.RoboVMWebService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageCache {
    private static final String PLACEHOLDER_URL = "http://store-app-images.robovm.com/placeholder.jpg";

    private static final ImageCache instance = new ImageCache();

    private ImageCache() {}

    public static ImageCache getInstance() {
        return instance;
    }

    private File saveLocation;

    private final OkHttpClient client = new OkHttpClient();

    public File getImage(String url) {
        Objects.requireNonNull(saveLocation, "Must specify a save location!");
        Objects.requireNonNull(url, "url");

        File destination = new File(saveLocation, FilenameUtils.getName(url));
        if (destination.exists()) {
            return destination;
        }
        return null;
    }

    public File downloadImage(String url) {
        return downloadImage(url, true);
    }

    private File downloadImage(String url, boolean retryOnFail) {
        Objects.requireNonNull(saveLocation, "Must specify a save location!");
        Objects.requireNonNull(url, "url");

        File destination = new File(saveLocation, FilenameUtils.getName(url));
        if (destination.exists()) {
            return destination;
        }

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            int code = response.code();
            if (code >= 200 && code < 300) { // Success
                InputStream in = response.body().byteStream();
                FileUtils.copyInputStreamToFile(in, destination);
                return destination;
            } else if (retryOnFail) { // Error
                return downloadImage(PLACEHOLDER_URL, false);
            }
        } catch (IOException e) {
            System.err.println("file download failed: " + e.getMessage());
            if (retryOnFail) {
                return downloadImage(PLACEHOLDER_URL, false);
            }
        }
        return null;
    }

    public void downloadImage(String url, Action<File> completion) {
        downloadImage(url, completion, true);
    }

    private void downloadImage(String url, Action<File> completion, boolean retryOnFail) {
        Objects.requireNonNull(saveLocation, "Must specify a save location!");
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(completion, "completion");

        File destination = new File(saveLocation, FilenameUtils.getName(url));
        if (destination.exists()) {
            RoboVMWebService.getInstance().getActionWrapper().invoke(completion, destination);
            return;
        }

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                int code = response.code();
                if (code >= 200 && code < 300) { // Success
                    InputStream in = response.body().byteStream();
                    FileUtils.copyInputStreamToFile(in, destination);
                    RoboVMWebService.getInstance().getActionWrapper().invoke(completion, destination);
                } else if (retryOnFail) { // Error
                    downloadImage(PLACEHOLDER_URL, completion, false);
                } else {
                    RoboVMWebService.getInstance().getActionWrapper().invoke(completion, null);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println("file download failed: " + e.getMessage());
                if (retryOnFail) {
                    downloadImage(PLACEHOLDER_URL, completion, false);
                } else {
                    RoboVMWebService.getInstance().getActionWrapper().invoke(completion, null);
                }
            }
        });
    }

    public void setSaveLocation(String saveLocation) {
        this.saveLocation = new File(saveLocation);
    }

    public File getSaveLocation() {
        return saveLocation;
    }
}
