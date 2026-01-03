package com.reymelin.GC;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Configuration Activity for Widget
 * Allows users to customize widget appearance and behavior
 */
public class WidgetConfigActivity extends Activity {
    
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private SharedPreferences prefs;
    
    private RadioGroup themeGroup;
    private RadioGroup timeFormatGroup;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_config);
        
        setResult(RESULT_CANCELED);
        
        // Get widget ID from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            );
        }
        
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        
        prefs = getSharedPreferences("widget_prefs", MODE_PRIVATE);
        
        initViews();
        setupListeners();
    }

    private void initViews() {
        themeGroup = findViewById(R.id.theme_group);
        timeFormatGroup = findViewById(R.id.time_format_group);
        confirmButton = findViewById(R.id.confirm_button);
        
        // Load saved preferences
        String savedTheme = prefs.getString("theme_" + appWidgetId, "metallic");
        String savedTimeFormat = prefs.getString("timeFormat_" + appWidgetId, "none");
        
        // Set default selections based on saved preferences
        selectTheme(savedTheme);
        selectTimeFormat(savedTimeFormat);
    }

    private void setupListeners() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration();
                
                // Update widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetConfigActivity.this);
                // Trigger widget update...
                
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
    }

    private void saveConfiguration() {
        SharedPreferences.Editor editor = prefs.edit();
        
        // Save theme
        int selectedThemeId = themeGroup.getCheckedRadioButtonId();
        RadioButton themeButton = findViewById(selectedThemeId);
        if (themeButton != null) {
            editor.putString("theme_" + appWidgetId, themeButton.getTag().toString());
        }
        
        // Save time format
        int selectedTimeFormatId = timeFormatGroup.getCheckedRadioButtonId();
        RadioButton timeFormatButton = findViewById(selectedTimeFormatId);
        if (timeFormatButton != null) {
            editor.putString("timeFormat_" + appWidgetId, timeFormatButton.getTag().toString());
        }
        
        editor.apply();
    }

    private void selectTheme(String theme) {
        for (int i = 0; i < themeGroup.getChildCount(); i++) {
            View child = themeGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                if (theme.equals(rb.getTag())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }
    }

    private void selectTimeFormat(String format) {
        for (int i = 0; i < timeFormatGroup.getChildCount(); i++) {
            View child = timeFormatGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                if (format.equals(rb.getTag())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }
    }
}
