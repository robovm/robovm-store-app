package org.robovm.store.util;

import org.robovm.store.api.GoogleAnalyticsService;

public class GoogleAnalyticsServiceDebug extends GoogleAnalyticsService {
    public void startAnalyticTracking() {
        logInfo("called startAnalyticTracking");
    }

    public void reportAnalyticScreen(String screen) {
        logInfo("called reportAnalyticScreen(" + screen + ")");
    }

    public void reportAnalyticEvent(String category, String action, String label, int value) {
        logInfo("called reportAnalyticEvent(" + category + ", " + action + ", " + label + ", " + value + ")");
    }

    public void logInfo(String info) {
    }
}
