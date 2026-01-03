package com.reymelin.gradientclock;

import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.webkit.WebView;
import android.webkit.WebSettings;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Gradient Clock Watch Face for Wear OS
 * Displays the animated gradient clock on smartwatches
 */
public class GradientClockWatchFaceService extends CanvasWatchFaceService {

    private static final long INTERACTIVE_UPDATE_RATE_MS = 16; // ~60fps
    private static final long AMBIENT_UPDATE_RATE_MS = 1000; // 1fps in ambient mode

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        
        private Calendar calendar;
        private final Handler updateTimeHandler = new Handler(Looper.getMainLooper());
        private boolean registeredTimeZoneReceiver = false;
        private boolean ambientMode = false;
        private boolean lowBitAmbient = false;
        private boolean burnInProtection = false;
        
        private Paint backgroundPaint;
        private Paint secondsPaint;
        private Paint minutesPaint;
        private Paint hoursPaint;
        private Paint textPaint;

        private final Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (isVisible()) {
                    invalidate();
                    long timeMs = System.currentTimeMillis();
                    long delayMs = ambientMode ? AMBIENT_UPDATE_RATE_MS : INTERACTIVE_UPDATE_RATE_MS;
                    long updateTimeMs = timeMs + delayMs - (timeMs % delayMs);
                    updateTimeHandler.postDelayed(this, updateTimeMs - timeMs);
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(GradientClockWatchFaceService.this)
                    .setAcceptsTapEvents(true)
                    .setHideNotificationIndicator(false)
                    .setShowSystemUiTime(false)
                    .build());

            calendar = Calendar.getInstance();

            initializePaints();
        }

        private void initializePaints() {
            backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.parseColor("#0a0a0a"));

            secondsPaint = new Paint();
            secondsPaint.setAntiAlias(true);
            secondsPaint.setStyle(Paint.Style.STROKE);
            secondsPaint.setStrokeWidth(8f);

            minutesPaint = new Paint();
            minutesPaint.setAntiAlias(true);
            minutesPaint.setStyle(Paint.Style.STROKE);
            minutesPaint.setStrokeWidth(6f);

            hoursPaint = new Paint();
            hoursPaint.setAntiAlias(true);
            hoursPaint.setStyle(Paint.Style.STROKE);
            hoursPaint.setStrokeWidth(4f);

            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(24f);
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void onDestroy() {
            updateTimeHandler.removeCallbacks(updateTimeRunnable);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            burnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            ambientMode = inAmbientMode;

            if (lowBitAmbient) {
                secondsPaint.setAntiAlias(!inAmbientMode);
                minutesPaint.setAntiAlias(!inAmbientMode);
                hoursPaint.setAntiAlias(!inAmbientMode);
                textPaint.setAntiAlias(!inAmbientMode);
            }

            invalidate();
            updateTimer();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                calendar.setTimeZone(TimeZone.getDefault());
                invalidate();
                updateTimer();
            } else {
                updateTimeHandler.removeCallbacks(updateTimeRunnable);
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            calendar.setTimeInMillis(now);

            // Draw background
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

            // Calculate center and radius
            float centerX = bounds.exactCenterX();
            float centerY = bounds.exactCenterY();
            float maxRadius = Math.min(centerX, centerY) * 0.9f;

            // Get time components
            int seconds = calendar.get(Calendar.SECOND);
            int minutes = calendar.get(Calendar.MINUTE);
            int hours = calendar.get(Calendar.HOUR);
            int milliseconds = calendar.get(Calendar.MILLISECOND);

            // Calculate angles (smooth)
            float secondsAngle = ((seconds + milliseconds / 1000f) / 60f) * 360f - 90f;
            float minutesAngle = ((minutes + seconds / 60f) / 60f) * 360f - 90f;
            float hoursAngle = ((hours + minutes / 60f) / 12f) * 360f - 90f;

            // Set colors based on time (simplified gradient effect)
            int secondsColor = Color.HSVToColor(new float[]{(secondsAngle + 90) % 360, 0.8f, 0.9f});
            int minutesColor = Color.HSVToColor(new float[]{(minutesAngle + 90) % 360, 0.7f, 0.8f});
            int hoursColor = Color.HSVToColor(new float[]{(hoursAngle + 90) % 360, 0.6f, 0.7f});

            secondsPaint.setColor(secondsColor);
            minutesPaint.setColor(minutesColor);
            hoursPaint.setColor(hoursColor);

            // Draw rings
            if (!ambientMode) {
                canvas.drawCircle(centerX, centerY, maxRadius, secondsPaint);
                canvas.drawCircle(centerX, centerY, maxRadius * 0.833f, minutesPaint);
                canvas.drawCircle(centerX, centerY, maxRadius * 0.583f, hoursPaint);
            } else {
                // Simplified display for ambient mode
                canvas.drawCircle(centerX, centerY, maxRadius * 0.5f, hoursPaint);
            }

            // Draw digital time in center
            String timeText = String.format("%02d:%02d:%02d", 
                calendar.get(Calendar.HOUR_OF_DAY), 
                minutes, 
                seconds);
            canvas.drawText(timeText, centerX, centerY + 10, textPaint);
        }

        private void updateTimer() {
            updateTimeHandler.removeCallbacks(updateTimeRunnable);
            if (isVisible()) {
                updateTimeHandler.post(updateTimeRunnable);
            }
        }
    }
}
