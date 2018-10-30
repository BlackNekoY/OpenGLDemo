package com.example.slimxu.opengldemo.light_direction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.slimxu.opengldemo.GLCamera;
import com.example.slimxu.opengldemo.R;

/**
 * Created by slimxu on 2018/10/12.
 */

public class LightDirectionActivity extends AppCompatActivity {

    private LightDirectionGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_direction);
        mGLSurfaceView = (LightDirectionGLSurfaceView)findViewById(R.id.surface_view);
    }

    public void goForward(View view) {
        mGLSurfaceView.mCamera.processKeyboardMovement(GLCamera.Direction.FORWARD, 0.5f);
    }

    public void backUp(View view) {
        mGLSurfaceView.mCamera.processKeyboardMovement(GLCamera.Direction.BACKWARD, 0.5f);
    }

    public void goLeft(View view) {
        mGLSurfaceView.mCamera.processKeyboardMovement(GLCamera.Direction.LEFT, 0.5f);
    }

    public void goRight(View view) {
        mGLSurfaceView.mCamera.processKeyboardMovement(GLCamera.Direction.RIGHT, 0.5f);
    }
}
