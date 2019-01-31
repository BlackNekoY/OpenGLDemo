package com.example.slimxu.opengldemo.egl_window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import com.example.slimxu.opengldemo.R;

public class EGLWindowActivity extends AppCompatActivity {

    private EGLSurfaceView mSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_window);

        mSurfaceView = (EGLSurfaceView) findViewById(R.id.egl_surface_view);
    }
}
