package com.reymelin.GC;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;
import android.widget.RemoteViews;

/**
 * Gradient Clock Home Screen Widget
 * Displays the animated gradient clock on the home screen
 */
public class GradientClockWidget extends AppWidgetProvider {
    
    private static final String ACTION_UPDATE = "com.reymelin.GC.ACTION_UPDATE_WIDGET";
    private static Handler updateHandler;
    private static Runnable updateRunnable;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update each widget instance
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        
        // Start periodic updates for smooth animation
        startPeriodicUpdates(context);
    }

    @Override
    public void onEnabled(Context context) {
        // First widget created
        super.onEnabled(context);
        startPeriodicUpdates(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Last widget removed
        super.onDisabled(context);
        stopPeriodicUpdates();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if (ACTION_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, GradientClockWidget.class)
            );
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gradient_clock_widget);
        
        // Set up click intent to open main app
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        
        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void startPeriodicUpdates(Context context) {
        if (updateHandler == null) {
            updateHandler = new Handler(Looper.getMainLooper());
            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    // Send broadcast to update widgets
                    Intent intent = new Intent(context, GradientClockWidget.class);
                    intent.setAction(ACTION_UPDATE);
                    context.sendBroadcast(intent);
                    
                    // Schedule next update (1 second for smooth animation)
                    updateHandler.postDelayed(this, 1000);
                }
            };
            updateHandler.post(updateRunnable);
        }
    }

    private static void stopPeriodicUpdates() {
        if (updateHandler != null && updateRunnable != null) {
            updateHandler.removeCallbacks(updateRunnable);
            updateHandler = null;
            updateRunnable = null;
        }
    }
}
