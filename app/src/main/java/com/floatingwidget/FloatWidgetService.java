package com.floatingwidget;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Service class responsible for managing the floating widget.
 * <p>
 * This service displays a floating widget on the screen that can be dragged around
 * and expanded or collapsed by user interaction. It uses WindowManager to add the
 * widget as a system overlay, allowing it to be displayed above other applications.
 * <p>
 * The floating widget consists of a collapsed view and an expanded view. The collapsed
 * view shows a minimal interface, and tapping on it expands the widget to show more
 * options. The widget can be closed by tapping on a close button.
 * <p>
 * The widget's position can be adjusted by dragging it across the screen. Touch events
 * are handled to update the widget's position dynamically.
 */
public class FloatWidgetService extends Service {

    private WindowManager mWindowManager; // Manages the windows for the floating widget
    private View mFloatingWidget; // The main view of the floating widget

    public FloatWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a dummy parent view for layout inflation
        ViewGroup parent = new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // No implementation needed for dummy parent view
            }
        };

        // Inflate the floating widget layout
        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, parent, false);

        // Set up layout parameters for the floating widget
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START; // Set initial gravity to top-left
        params.x = 0; // Initial X position
        params.y = 100; // Initial Y position

        // Obtain WindowManager service and add the floating widget to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);

        // Retrieve views within the floating widget
        final View collapsedView = mFloatingWidget.findViewById(R.id.collapse_view);
        final View expandedView = mFloatingWidget.findViewById(R.id.expanded_container);

        // Set click listener for close button in collapsed view
        ImageView closeButtonCollapsed = mFloatingWidget.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(view -> stopSelf());

        // Set click listener for close button in expanded view
        ImageView closeButton = mFloatingWidget.findViewById(R.id.close_button);
        closeButton.setOnClickListener(view -> {
            collapsedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
        });

        // Set touch listener for dragging the floating widget
        mFloatingWidget.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Capture initial touch and widget positions
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Check for minimal movement to distinguish between tap and drag
                        int XDiff = (int) (event.getRawX() - initialTouchX);
                        int YDiff = (int) (event.getRawY() - initialTouchY);
                        if (XDiff < 10 && YDiff < 10) {
                            // Toggle between collapsed and expanded views on tap
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                                // Perform click on expanded view
                                expandedView.performClick();
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Update widget position on drag
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingWidget, params);
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Checks if the floating widget view is currently collapsed.
     *
     * @return true if the widget is collapsed or mFloatingWidget is null, false otherwise
     */
    private boolean isViewCollapsed() {
        return mFloatingWidget == null || mFloatingWidget.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the floating widget from WindowManager when the service is destroyed
        if (mFloatingWidget != null) {
            mWindowManager.removeView(mFloatingWidget);
        }
    }
}
