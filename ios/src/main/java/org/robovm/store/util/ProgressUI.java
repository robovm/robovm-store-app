package org.robovm.store.util;

import org.robovm.apple.uikit.UIViewController;
import org.robovm.store.views.LoadingView;

public class ProgressUI {
    private static final LoadingView loadingView = new LoadingView();

    public static void show(UIViewController controller) {
        loadingView.show(controller);
    }

    public static void show(String message, UIViewController controller) {
        loadingView.setMessage(message);
        loadingView.show(controller);
    }

    public static void hide() {
        loadingView.hide();
    }
}
