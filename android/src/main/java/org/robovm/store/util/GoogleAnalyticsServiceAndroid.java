package org.robovm.store.util;

import org.robovm.store.api.GoogleAnalyticsService;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import android.content.Context;

public class GoogleAnalyticsServiceAndroid extends GoogleAnalyticsService {
    private static GoogleAnalytics analytics;
    private static Tracker tracker;
    private Context context;

    public GoogleAnalyticsServiceAndroid(Context context) {
        this.context = context;
        startAnalyticTracking();
    }

    public void startAnalyticTracking() {
        if (analytics == null && !showInstructions()) {
            analytics = GoogleAnalytics.getInstance(context);
            analytics.setDryRun(GA_IS_DRY_RUN);
            analytics.setLocalDispatchPeriod(GA_DISPATCH_INTERVAL);
            tracker = analytics.newTracker(GA_PROPERTY_ID);
        }
    }

    public void reportAnalyticEvent(String category, String action, String label, int value) {
        if (tracker != null) {
            tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value).build());
        }
    }

    public void reportAnalyticScreen(String screenName) {
        if (tracker != null) {
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }


}
