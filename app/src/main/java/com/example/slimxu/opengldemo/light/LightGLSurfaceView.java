package com.example.slimxu.opengldemo.light;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.slimxu.opengldemo.GLCamera;
import com.example.slimxu.opengldemo.GLUtil;
import com.example.slimxu.opengldemo.Vector;

import junit.framework.Assert;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by slimxu on 2018/10/18.
 */

public class LightGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private final float VERTEX_ARRAY[] = {
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    };
    private FloatBuffer mVertexBuffer;

    private final String VERTEX_SHADER =
            "#version 300 es \n" +
            "uniform mat4 model; \n" +
            "uniform mat4 view; \n" +
            "uniform mat4 projection; \n" +
            "layout (location = 0) in vec3 pos; \n" +
            "void main() { \n" +
            "   gl_Position = projection * view * model * vec4(pos, 1.0f); \n" +
            "} \n";
    private final String FRAGMENT_SHADER =
            "#version 300 es \n" +
            "precision mediump float; \n" +
            "uniform vec3 objectColor; \n" +
            "uniform vec3 lightColor; \n" +
            "out vec4 color; \n" +
            "void main() { \n" +
            "   color = vec4(objectColor * lightColor, 1.0f); \n" +
            "} \n";

    private int mProgram;

    private int mObjVAO;
    private int mLightVAO;
    private int mVBO;

    public GLCamera mCamera = new GLCamera(new Vector(0, 0, 5));
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    private int mWidth;
    private int mHeight;

    private float mLastTouchX;
    private float mLastTouchY;

    private Vector mObjectPos = new Vector(1.2f, 1f, 2.0f);

    public LightGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public LightGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVertexBuffer = GLUtil.array2FloatBuffer(VERTEX_ARRAY);
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastTouchX = x;
                        mLastTouchY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float disX = x - mLastTouchX;
                        float disY = mLastTouchY - y;

                        mCamera.processPointerMovement(disX, disY);

                        mLastTouchX = x;
                        mLastTouchY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                requestRender();
                return true;
            }
        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        mProgram = GLUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        checkProgram();

        // gen VAO
        int[] vaos = new int[2];
        GLES30.glGenVertexArrays(2, vaos, 0);
        mObjVAO = vaos[0];
        mLightVAO = vaos[1];

        // gen VBO
        int[] vbos = new int[1];
        GLES30.glGenBuffers(1, vbos, 0);
        mVBO = vbos[0];

        GLES30.glBindVertexArray(mObjVAO);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO);
        GLES30.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mProgram, "pos"), 3, GLES30.GL_FLOAT, false, 5 * 4, 0);
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mProgram, "pos"));
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        GLES30.glBindVertexArray(0);

//        GLES30.glBindVertexArray(mLightVAO);
//        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO);
//        GLES30.glVertexAttribPointer(mAttrPosLocation, 3, GLES30.GL_FLOAT, false, 5 * 4, 0);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        checkProgram();
        GLES30.glViewport(0, 0, width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        checkProgram();
        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);


        int unifModelPointer = GLES30.glGetUniformLocation(mProgram, "model");
        int unifViewPointer = GLES30.glGetUniformLocation(mProgram, "view");
        int unifProjectionPointer = GLES30.glGetUniformLocation(mProgram, "projection");
        int unifObjectColor = GLES30.glGetUniformLocation(mProgram, "objectColor");
        int unifLightColor = GLES30.glGetUniformLocation(mProgram, "lightColor");

        // model
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mObjectPos.x, mObjectPos.y, mObjectPos.z);
        Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
        GLES30.glUniformMatrix4fv(unifModelPointer, 1, false, mModelMatrix, 0);

        // view
        GLES30.glUniformMatrix4fv(unifViewPointer, 1, false, mCamera.getViewMatrix(), 0);

        // projection
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.zoom, (float)mWidth / mHeight, 0.1f, 100f);
        GLES30.glUniformMatrix4fv(unifProjectionPointer, 1, false, mProjectionMatrix, 0);

        // objectColor
        GLES30.glUniform3f(unifObjectColor, 1.0f, 0.5f, 0.31f);

        // lightColor
        GLES30.glUniform3f(unifLightColor, 1.0f, 1f, 1.0f);

        GLES30.glBindVertexArray(mObjVAO);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glBindVertexArray(0);
    }

    public void goForward() {
        mCamera.processKeyboardMovement(GLCamera.Direction.FORWARD, 0.5f);
        requestRender();
    }

    public void backUp() {
        mCamera.processKeyboardMovement(GLCamera.Direction.BACKWARD, 0.5f);
        requestRender();
    }

    public void goLeft() {
        mCamera.processKeyboardMovement(GLCamera.Direction.LEFT, 0.5f);
        requestRender();
    }

    public void goRight() {
        mCamera.processKeyboardMovement(GLCamera.Direction.RIGHT, 0.5f);
        requestRender();
    }

    private void checkProgram() {
        Assert.assertTrue(mProgram != -1);
    }
}
