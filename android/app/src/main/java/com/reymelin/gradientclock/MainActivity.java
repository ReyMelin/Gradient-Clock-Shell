package com.reymelin.gradientclock;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;

/**
 * Optimized MainActivity for Gradient Clock
 * Includes performance optimizations, wake lock, and fullscreen support
 */
public class MainActivity extends BridgeActivity {
    
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable immersive fullscreen mode
        enableImmersiveMode();
        
        // Optimize WebView performance
        optimizeWebView();
        
        // Keep screen on (optional, can be controlled by user preference)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Allow brightness control
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableImmersiveMode();
        acquireWakeLock();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    /**
     * Enable immersive fullscreen mode for distraction-free clock display
     */
    private void enableImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    /**
     * Optimize WebView for smooth animation performance
     */
    private void optimizeWebView() {
        WebView bridge = getBridge().getWebView();
        if (bridge != null) {
            WebSettings settings = bridge.getSettings();
            
            // Enable hardware acceleration
            bridge.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            
            // Optimize rendering
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            
            // Enable caching
            settings.setAppCacheEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            
            // Optimize JavaScript execution
            settings.setJavaScriptCanOpenWindowsAutomatically(false);
            
            // Disable unnecessary features for performance
            settings.setGeolocationEnabled(false);
            settings.setSupportZoom(false);
            settings.setBuiltInZoomControls(false);
            settings.setDisplayZoomControls(false);
            
            // Enable DOM storage for local preferences
            settings.setDomStorageEnabled(true);
        }
    }

    /**
     * Acquire partial wake lock to keep CPU running for smooth animations
     */
    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "GradientClock::AnimationWakeLock"
            );
            wakeLock.setReferenceCounted(false);
        }
        
        if (!wakeLock.isHeld()) {
            wakeLock.acquire(10*60*1000L /*10 minutes*/);
        }
    }

    /**
     * Release wake lock to save battery
     */
    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }
}
