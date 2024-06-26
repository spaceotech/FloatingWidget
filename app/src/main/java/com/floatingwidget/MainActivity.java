package com.floatingwidget;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity is responsible for initializing the view and handling overlay permission requests.
 * <p>
 * The MainActivity checks if the app has the overlay permission (required for drawing over other apps)
 * on devices running Android Marshmallow (API level 23) and above. If the permission is not granted,
 * it launches an activity to request the permission. Once the permission is granted, it initializes
 * the view by setting up a button click listener to start the FloatWidgetService.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ActivityResultLauncher for requesting overlay permission
        ActivityResultLauncher<Intent> requestStartActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Check if the build version is >= Marshmallow
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Verify if the overlay permission is granted
                        if (Settings.canDrawOverlays(this)) {
                            // Permission granted, proceed with adding the overlay
                            initializeView();
                        } else {
                            // Permission denied, show a toast message
                            Toast.makeText(this, getString(R.string.msg_draw_over_other_app_permission_not_enabled), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Check if the build version is >= Marshmallow and overlay permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Create an intent to request overlay permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            // Launch the overlay permission request
            requestStartActivityForResult.launch(intent);
        } else {
            // If permission is already granted, initialize the view
            initializeView();
        }
    }

    /**
     * Initializes the view by setting up the button click listener.
     * The button click listener starts the FloatWidgetService and finishes the activity.
     */
    private void initializeView() {
        Button mButton = findViewById(R.id.createBtn);

        mButton.setOnClickListener(view -> {
            // Start the FloatWidgetService when the button is clicked and finish the current activity
            startService(new Intent(MainActivity.this, FloatWidgetService.class));
            finish();
        });
    }
}
