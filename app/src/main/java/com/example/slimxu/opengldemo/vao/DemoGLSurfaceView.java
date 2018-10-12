package com.example.slimxu.opengldemo.vao;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by slimxu on 2018/9/28.
 */

public class DemoGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    public static final String VERTEX_SHADER =
                    "#version 300 es \n" +
                    "layout (location = 0) in vec3 pos; \n" +
                    "layout (location = 1) in vec4 color; \n" +
                    "out vec4 f_color; \n" +
                    "void main() { \n" +
                        "gl_Position = vec4(pos, 1.0f); \n" +
                        "f_color = color; \n" +
                    "}\n";
    public static final String FRAGMENT_SHADER =
                    "#version 300 es \n" +
                    "precision mediump float; \n" +
                    "in vec4 f_color; \n" +
                    "out vec4 fragColor; \n" +
                    "void main() { \n" +
                        "fragColor = f_color; \n" +
                    "}\n";
    public static final float[] VERTEX_ARRAY = {
            // 位置              // 颜色
            0, 1, 0,            1, 0, 0, 1,
            0, 0, 0,            0, 1, 0, 1,
            1, 0, 0,            0, 0, 1, 1
    };
    private FloatBuffer mVertexBuf;

    private int mProgram;
    private int mPosHandle;
    private int mColorHandle;

    private int mTriangleVAO;
    private int mTriangleVBO;

    public DemoGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public DemoGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initBuffer();

        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    private void initBuffer() {
        mVertexBuf = ByteBuffer.allocateDirect(VERTEX_ARRAY.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(VERTEX_ARRAY);
        mVertexBuf.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0, 0, 1.0f);

        int vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        int fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(vertexShader, VERTEX_SHADER);
        GLES30.glShaderSource(fragmentShader, FRAGMENT_SHADER);
        GLES30.glCompileShader(vertexShader);
        GLES30.glCompileShader(fragmentShader);

        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader);
        GLES30.glAttachShader(mProgram, fragmentShader);
        GLES30.glLinkProgram(mProgram);

        int[] status = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES30.GL_TRUE) {
            Log.e("ES30_ERROR", "can not link program");
            GLES30.glDeleteProgram(mProgram);
        }
        mPosHandle = GLES30.glGetAttribLocation(mProgram, "pos");
        mColorHandle = GLES30.glGetAttribLocation(mProgram, "color");

        onSurfaceCreatedVAO();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        onDrawVAO();
    }

    private void onSurfaceCreatedVBO () {
        int[] vbos = new int[1];
        GLES30.glGenBuffers(1, vbos, 0);
        mTriangleVBO = vbos[0];
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTriangleVBO);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mVertexBuf.capacity() * 4, mVertexBuf, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    private void onDrawVBO () {
        GLES30.glUseProgram(mProgram);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTriangleVBO);
        GLES30.glEnableVertexAttribArray(mPosHandle);
        GLES30.glVertexAttribPointer(mPosHandle, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }

    private void onSurfaceCreatedVAO () {
        int[] vaos = new int[1];
        GLES30.glGenVertexArrays(1, vaos, 0);
        mTriangleVAO = vaos[0];
        GLES30.glBindVertexArray(mTriangleVAO);

        int[] vbos = new int[1];
        GLES30.glGenBuffers(1, vbos, 0);
        mTriangleVBO = vbos[0];
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTriangleVBO);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mVertexBuf.capacity() * 4, mVertexBuf, GLES30.GL_STATIC_DRAW);
        // 关键 使用GPU中的缓冲数据，不再从RAM中取数据，所以后面的2个参数都是0
        GLES30.glVertexAttribPointer(mPosHandle, 3, GLES30.GL_FLOAT, false, 7 * 4, 0);
        GLES30.glVertexAttribPointer(mColorHandle, 4, GLES30.GL_FLOAT, false, 7 * 4, 3 * 4);
//        GLES30.glVertexAttribPointer(mPosHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuf);
        GLES30.glEnableVertexAttribArray(mPosHandle);
        GLES30.glEnableVertexAttribArray(mColorHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        GLES30.glBindVertexArray(0);
    }

    private void onDrawVAO() {
        GLES30.glUseProgram(mProgram);
        GLES30.glBindVertexArray(mTriangleVAO);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
        GLES30.glBindVertexArray(0);
    }


    public static void checkGLError(String op){
        int error;
        //错误代码不为0, 就打印错误日志, 并抛出异常
        while( (error = GLES30.glGetError()) != GLES30.GL_NO_ERROR ){
            Log.e("ES30_ERROR", op + ": glError " + error);
        }
    }


}
