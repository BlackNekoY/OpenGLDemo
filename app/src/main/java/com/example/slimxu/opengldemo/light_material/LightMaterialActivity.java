package com.example.slimxu.opengldemo.light_material;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.slimxu.opengldemo.R;
import com.example.slimxu.opengldemo.light.LightGLSurfaceView;

/**
 * Created by slimxu on 2018/10/22.
 */

public class LightMaterialActivity extends AppCompatActivity {
    private LightMaterialGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_material);
        mGLSurfaceView = (LightMaterialGLSurfaceView)findViewById(R.id.surface_view);
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
