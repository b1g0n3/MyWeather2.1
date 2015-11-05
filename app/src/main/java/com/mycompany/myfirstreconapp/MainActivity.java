package com.mycompany.myfirstreconapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.metrics.HUDMetricsID;
import com.reconinstruments.os.metrics.HUDMetricsManager;
import com.reconinstruments.os.metrics.MetricsValueChangedListener;
import android.app.Notification;
import android.app.NotificationManager;

public class MainActivity extends Activity implements MetricsValueChangedListener
{
    HUDMetricsManager mHUDMetricsManager;
    NotificationManager mNotificationManager;

    TextView altitudeTextView;
    TextView speedVrtTextView;

    float altitudePressure = 0;
    float speedVertical    = 0;

    float startPressure = Float.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        altitudeTextView = (TextView) findViewById(R.id.altitudeTextView);

        speedVrtTextView = (TextView) findViewById(R.id.speedTextView);
        speedVrtTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 52);

        mHUDMetricsManager   = (HUDMetricsManager) HUDOS.getHUDService(HUDOS.HUD_METRICS_SERVICE);
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.ALTITUDE_PRESSURE);
        mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.SPEED_VERTICAL);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.ALTITUDE_PRESSURE);
        mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.SPEED_VERTICAL);
    }

    @Override
    public void onMetricsValueChanged(int metricID, float value, long changeTime, boolean isValid)
    {
        switch(metricID)
        {
            case HUDMetricsID.ALTITUDE_PRESSURE :
                altitudePressure = value;
                altitudeTextView.setText(String.format("%.1f", value) + " m");
                break;
            case HUDMetricsID.SPEED_VERTICAL :
                speedVertical = value;
                speedVrtTextView.setText(String.format("%.1f", value) + " km/h");
                break;
        }

        if(Math.abs(startPressure - altitudePressure) > 2)
        {
            if(startPressure != Float.MIN_VALUE){ CreateNotification(); }
            startPressure = altitudePressure;
        }
    }

    private void CreateNotification()
    {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Notification")
                .setSmallIcon(R.drawable.icon_checkmark)
                .setContentText("Altitude Changed (+/- 2 Meters)!")
                .build();
        mNotificationManager.notify(0, notification);
    }
}