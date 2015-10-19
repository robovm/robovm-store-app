package org.robovm.store.views;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.store.util.Colors;

public class LoadingView extends UIView {
    private final UIActivityIndicatorView activityView;
    private final UILabel loadingLabel;

    public LoadingView() {
        this(getDefaultFrame());
    }

    private static CGRect getDefaultFrame() {
        CGRect bounds = UIScreen.getMainScreen().getBounds();
        double width = bounds.getWidth() * 0.75;
        double height = bounds.getHeight() * 0.22;
        double x = (bounds.getWidth() - width) / 2;
        double y = (bounds.getHeight() - height) / 2;

        return new CGRect(x, y, width, height);
    }

    public LoadingView(CGRect frame) {
        super(frame);
        setBackgroundColor(UIColor.fromRGBA(0, 0, 0, 0.5));
        setClipsToBounds(true);
        getLayer().setCornerRadius(10);

        activityView = new UIActivityIndicatorView(UIActivityIndicatorViewStyle.WhiteLarge);
        activityView.setCenter(new CGPoint(frame.getWidth() / 2, frame.getHeight() * 0.35));
        addSubview(activityView);

        loadingLabel = new UILabel(new CGRect(0, frame.getHeight() * 0.7, frame.getWidth(), 22));
        loadingLabel.setBackgroundColor(Colors.Clear);
        loadingLabel.setTextColor(Colors.White);
        loadingLabel.setAdjustsFontSizeToFitWidth(true);
        loadingLabel.setTextAlignment(NSTextAlignment.Center);
        loadingLabel.setText("Loading...");
        addSubview(loadingLabel);
    }

    public void setMessage(String message) {
        loadingLabel.setText(message);
    }

    public void show(UIViewController controller) {
        controller.getView().addSubview(this);
        activityView.startAnimating();
    }

    public void hide() {
        activityView.stopAnimating();
        removeFromSuperview();
    }
}
