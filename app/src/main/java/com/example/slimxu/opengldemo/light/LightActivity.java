package com.example.slimxu.opengldemo.light;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.slimxu.opengldemo.GLCamera;
import com.example.slimxu.opengldemo.R;
import com.example.slimxu.opengldemo.camera.CameraGLSurfaceView;

/**
 * Created by slimxu on 2018/10/18.
 */

public class LightActivity extends AppCompatActivity {

    private LightGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        mGLSurfaceView = (LightGLSurfaceView)findViewById(R.id.surface_view);
    }

    public void goForward(View view) {
        mGLSurfaceView.goForward();
    }

    public void backUp(View view) {
        mGLSurfaceView.backUp();
    }

    public void goLeft(View view) {
        mGLSurfaceView.goLeft();
    }

    public void goRight(View view) {
        mGLSurfaceView.goRight();
    }
}
