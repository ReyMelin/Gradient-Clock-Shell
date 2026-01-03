package com.reymelin.gradientclock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Settings Activity for Live Wallpaper
 * Allows users to customize wallpaper appearance
 */
public class WallpaperSettingsActivity extends Activity {
    
    private SharedPreferences prefs;
    private RadioGroup themeGroup;
    private RadioGroup timeFormatGroup;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_settings);
        
        prefs = getSharedPreferences("wallpaper_prefs", MODE_PRIVATE);
        
        initViews();
        setupListeners();
    }

    private void initViews() {
        themeGroup = findViewById(R.id.theme_group);
        timeFormatGroup = findViewById(R.id.time_format_group);
        saveButton = findViewById(R.id.save_button);
        
        // Load saved preferences
        String savedTheme = prefs.getString("theme", "metallic");
        String savedTimeFormat = prefs.getString("timeFormat", "none");
        
        selectTheme(savedTheme);
        selectTimeFormat(savedTimeFormat);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration();
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
            editor.putString("theme", themeButton.getTag().toString());
        }
        
        // Save time format
        int selectedTimeFormatId = timeFormatGroup.getCheckedRadioButtonId();
        RadioButton timeFormatButton = findViewById(selectedTimeFormatId);
        if (timeFormatButton != null) {
            editor.putString("timeFormat", timeFormatButton.getTag().toString());
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
