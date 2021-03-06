package com.example.slimxu.opengldemo.light_texture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.slimxu.opengldemo.R;

/**
 * Created by slimxu on 2018/10/22.
 */

public class LightTextureActivity extends AppCompatActivity {
    private LightTextureGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_texture);
        mGLSurfaceView = (LightTextureGLSurfaceView)findViewById(R.id.surface_view);
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

    public void up(View view) {
        mGLSurfaceView.up();
    }

    public void down(View view) {
        mGLSurfaceView.down();
    }
}
