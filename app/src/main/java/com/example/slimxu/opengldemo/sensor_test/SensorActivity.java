package com.example.slimxu.opengldemo.sensor_test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.slimxu.opengldemo.R;

/**
 * Created by slimxu on 2018/10/4.
 */

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorGLSurfaceView mSurfaceView;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mSurfaceView = (SensorGLSurfaceView) findViewById(R.id.surface_view);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[1];
        if (x > 0) {
            x = 0;
        }
        if (x < -90) {
            x = -90;
        }

        x = Math.abs(x);
        mSurfaceView.rotateXValue = 45 - (x / 90) * 45f; // 45 ~ 0
        mSurfaceView.requestRender();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
