package com.reymelin.GC;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.view.MotionEvent;

/**
 * Gradient Clock Live Wallpaper
 * Displays the animated gradient clock as a live wallpaper
 */
public class GradientClockWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new GradientClockEngine();
    }

    private class GradientClockEngine extends Engine {
        
        private Handler handler = new Handler(Looper.getMainLooper());
        private WebView webView;
        private boolean visible = false;
        private final Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    draw();
                    handler.postDelayed(this, 16); // ~60fps
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            
            // Initialize WebView for rendering the clock
            webView = new WebView(GradientClockWallpaperService.this);
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setAllowContentAccess(true);
            
            // Load the clock HTML
            webView.loadUrl("file:///android_asset/public/index.html");
            webView.setBackgroundColor(0x00000000); // Transparent
            webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(updateRunnable);
            } else {
                handler.removeCallbacks(updateRunnable);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            // Pass touch events to WebView
            if (webView != null) {
                webView.dispatchTouchEvent(event);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (webView != null) {
                webView.layout(0, 0, width, height);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(updateRunnable);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(updateRunnable);
            if (webView != null) {
                webView.destroy();
                webView = null;
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null && webView != null) {
                    // Draw WebView content to canvas
                    canvas.drawColor(0xFF0a0a0a); // Background color
                    webView.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
