package com.example.slimxu.opengldemo.sensor_test;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.example.slimxu.opengldemo.GLUtil;
import com.example.slimxu.opengldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by slimxu on 2018/10/4.
 */

public class SensorGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private final float[] VERTEX_ARRAY = {
            // 位置顶点    // 纹理顶点
            -1, 1, 0,   0, 1,
            1, 1, 0,    1, 1,
            -1, -1, 0,  0, 0,
            1, -1, 0,   1, 0
    };

    private FloatBuffer mVertexBuf;

    private final String VERTEX_SHADER =
            "#version 300 es \n" +
            "layout (location = 0) in vec3 pos; \n" +
            "layout (location = 1) in vec2 texPos; \n" +
            "uniform mat4 model; \n" +
            "uniform mat4 view;  \n" +
            "uniform mat4 projection; \n" +
            "out vec2 f_texPos; \n" +
            "void main() { \n" +
            "   gl_Position = projection * view * model * vec4(pos, 1); \n" +
            "   f_texPos = texPos; \n" +
            "}";
    private final String FRAGMENT_SHADER =
            "#version 300 es \n" +
            "precision mediump float; \n" +
            "in vec2 f_texPos; \n" +
            "uniform sampler2D texture1; \n" +
            "uniform sampler2D texture2; \n" +
            "out vec4 color; \n" +
            "void main() { \n" +
            "   color = mix(texture(texture1, f_texPos), texture(texture2, f_texPos), 0.2); \n" +
            "}";
    private int mProgram;
    private int mPosHandle;
    private int mTexPosHandle;
    private int mModelHandle;
    private int mViewHandle;
    private int mProjectionHandle;

    private int[] mTex = new int[2];
    private int mTexture1Handle;
    private int mTexture2Handle;
    private int VAO;

    private float ratio;

    public float rotateXValue;  // 绕着X轴旋转的角度

    public SensorGLSurfaceView(Context context) {
        super(context);
        init();
    }


    public SensorGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVertexBuf = array2FloatBuffer(VERTEX_ARRAY);

        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    private FloatBuffer array2FloatBuffer(float[] array) {
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(array.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(array);
        floatBuffer.position(0);
        return floatBuffer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        mProgram = GLUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (mProgram < 0) {
            throw new IllegalStateException("create program error");
        }
        mPosHandle = GLES30.glGetAttribLocation(mProgram, "pos");
        mTexPosHandle = GLES30.glGetAttribLocation(mProgram, "texPos");
        mModelHandle = GLES30.glGetUniformLocation(mProgram, "model");
        mViewHandle = GLES30.glGetUniformLocation(mProgram, "view");
        mProjectionHandle = GLES30.glGetUniformLocation(mProgram, "projection");

        mTexture1Handle = GLES30.glGetUniformLocation(mProgram, "texture1");
        mTexture2Handle = GLES30.glGetUniformLocation(mProgram, "texture2");

        int[] vaos = new int[1];
        GLES30.glGenVertexArrays(1, vaos, 0);
        VAO = vaos[0];
        GLES30.glBindVertexArray(VAO);

        int[] vbos = new int[1];
        GLES30.glGenBuffers(1, vbos, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbos[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mVertexBuf.capacity() * 4, mVertexBuf, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(mPosHandle, 3, GLES30.GL_FLOAT, false, 5 * 4, 0);
        GLES30.glVertexAttribPointer(mTexPosHandle, 2, GLES30.GL_FLOAT, false, 5 * 4, 3 * 4);
        GLES30.glEnableVertexAttribArray(mPosHandle);
        GLES30.glEnableVertexAttribArray(mTexPosHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        GLES30.glBindVertexArray(0);

        // 建两个纹理id
        GLES30.glGenTextures(2, mTex, 0);

        // 初始化第一个纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTex[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(getResources(), R.drawable.wall), 0);

        // 初始化第二个纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTex[1]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(getResources(), R.drawable.awesomeface), 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        ratio = (float)width / height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glUseProgram(mProgram);

        // 第一个纹理id 对应纹理单元0
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTex[0]);
        GLES30.glUniform1i(mTexture1Handle, 0);

        // 第二个纹理id 对应纹理单元1
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTex[1]);
        GLES30.glUniform1i(mTexture2Handle, 1);

        // VAO
        GLES30.glBindVertexArray(VAO);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glBindVertexArray(0);

        // uniform Matrix
        float[] matrix = new float[16];

        Matrix.setIdentityM(matrix, 0);
        Matrix.rotateM(matrix, 0, -rotateXValue, 1, 0, 0);
        GLES30.glUniformMatrix4fv(mModelHandle, 1, false, matrix, 0);

        Matrix.setIdentityM(matrix, 0);
        Matrix.setLookAtM(matrix, 0,
                0, 0, 3,
                0, 0, 0,
                0, 1, 0);
        GLES30.glUniformMatrix4fv(mViewHandle, 1, false, matrix, 0);

        Matrix.setIdentityM(matrix, 0);
        Matrix.perspectiveM(matrix, 0, 45f, ratio, 0.1f, 100.f);
        GLES30.glUniformMatrix4fv(mProjectionHandle, 1, false, matrix, 0);

        /**
         * 你可能注意到纹理上下颠倒了！这是因为OpenGL要求y轴0.0坐标是在图片的底部的，但是图片的y轴0.0坐标通常在顶部。
         */
    }
}
