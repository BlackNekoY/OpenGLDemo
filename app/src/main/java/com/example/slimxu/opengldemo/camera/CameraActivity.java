package com.example.slimxu.opengldemo.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.slimxu.opengldemo.R;

/**
 * Created by slimxu on 2018/10/12.
 */

public class CameraActivity extends AppCompatActivity {

    private CameraGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mGLSurfaceView = (CameraGLSurfaceView)findViewById(R.id.surface_view);
    }

    public void goForward(View view) {
        mGLSurfaceView.mCameraMoveZValue -= 0.5f;
    }

    public void backUp(View view) {
        mGLSurfaceView.mCameraMoveZValue += 0.5f;
    }

    public void goLeft(View view) {
        mGLSurfaceView.mCameraMoveXValue -= 0.5f;
    }

    public void goRight(View view) {
        mGLSurfaceView.mCameraMoveXValue += 0.5f;
    }
}
