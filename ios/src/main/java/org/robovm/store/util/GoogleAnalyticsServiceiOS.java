package org.robovm.store.util;

import org.robovm.apple.foundation.NSNumber;
import org.robovm.pods.google.analytics.GAIDictionaryBuilder;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.pods.google.analytics.GAI;
import org.robovm.pods.google.analytics.GAIFields;
import org.robovm.pods.google.analytics.GAITracker;

public class GoogleAnalyticsServiceiOS extends GoogleAnalyticsService {
    public static GAITracker gaiTracker;

    public GoogleAnalyticsServiceiOS() {
        startAnalyticTracking();
    }

    public void startAnalyticTracking() {
        if (gaiTracker == null && !showInstructions()) {
            GAI gai = GAI.getSharedInstance();
            gai.setDryRun(GA_IS_DRY_RUN);
            gai.setDispatchInterval(GA_DISPATCH_INTERVAL);
            gaiTracker = gai.getTracker(GA_PROPERTY_ID);
        }
    }

    public void reportAnalyticEvent(String category, String action, String label, int value) {
        if (gaiTracker != null) {
            gaiTracker.send(GAIDictionaryBuilder.createEvent(category, action, label, NSNumber.valueOf(value)).build());
        }
    }

    public void reportAnalyticScreen(String screenName) {
        if (gaiTracker != null) {
            gaiTracker.put(GAIFields.ScreenName(), screenName);
            gaiTracker.send(GAIDictionaryBuilder.createScreenView().build());
        }
    }

}
