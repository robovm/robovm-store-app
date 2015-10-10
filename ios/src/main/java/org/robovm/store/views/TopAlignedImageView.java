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

import java.io.File;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.ImageCache;

public class TopAlignedImageView extends UIView {
    private CGSize originalSize;
    private final UIImageView imageView;
    private UIImage image;
    private UIActivityIndicatorView progress;

    public TopAlignedImageView() {
        setClipsToBounds(true);
        imageView = new UIImageView();
        addSubview(imageView);

        addSubview(progress = new UIActivityIndicatorView(UIActivityIndicatorViewStyle.WhiteLarge));
        setTranslatesAutoresizingMaskIntoConstraints(false);
    }

    public UIImage getImage() {
        return image;
    }

    public void setImage(UIImage image) {
        this.image = image;
        originalSize = image == null ? CGSize.Zero() : image.getSize();
        imageView.setImage(this.image = image);
        layoutSubviews();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        progress.setCenter(getCenter());
        if (originalSize == null || CGSize.Zero().equals(originalSize)) {
            return;
        }
        CGRect frame = getBounds();
        double scale = frame.getWidth() / originalSize.getWidth();
        frame.setHeight(originalSize.getHeight() * scale);
        imageView.setFrame(frame);
    }

    public void loadUrl(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        File image = ImageCache.getInstance().getImage(url);
        if (image != null) {
            setImage(new UIImage(image));
        } else {
            progress.startAnimating();
            ImageCache.getInstance().downloadImage(url, (file) -> {
                if (file != null) {
                    UIView.animate(.3,
                            () -> setImage(new UIImage(file)),
                            (success) -> progress.stopAnimating());
                } else {
                    progress.stopAnimating();
                }
            });
        }
    }
}
