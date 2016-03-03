package org.robovm.store.api;

import org.robovm.store.util.GoogleAnalyticsServiceDebug;

public abstract class GoogleAnalyticsService {
    // TODO: Enter your Google Analytics Property ID here
    public static final String GA_PROPERTY_ID = ""; // example "UA-XXXXXXXX-1"
    public static final int GA_DISPATCH_INTERVAL = 30;
    public static final boolean GA_IS_DRY_RUN = false;

    private static GoogleAnalyticsService instance = null;

    public static void setInstance(GoogleAnalyticsService service) {
        instance = service;
    }

    public static GoogleAnalyticsService getInstance() {
        if (instance == null) {
            setInstance(new GoogleAnalyticsServiceDebug());
        }
        return instance;
    }

    public boolean showInstructions() {
        return (GA_PROPERTY_ID == null || GA_PROPERTY_ID.isEmpty());
    }

    public abstract void startAnalyticTracking();

    public abstract void reportAnalyticScreen(String screen);

    public abstract void reportAnalyticEvent(String category, String action, String label, int value);

}
