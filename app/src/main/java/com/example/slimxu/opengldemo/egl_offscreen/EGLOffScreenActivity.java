package com.example.slimxu.opengldemo.egl_offscreen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.EGL14;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.slimxu.opengldemo.DisplayUtil;
import com.example.slimxu.opengldemo.GLUtil;
import com.example.slimxu.opengldemo.R;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * 创建EGL环境进行离屏渲染
 */
public class EGLOffScreenActivity extends AppCompatActivity {


    private ImageView mImage;
    private int mWidth;
    private int mHeight;

    // EGL...
    private EGL10 mEgl;
    private EGLDisplay mEGLDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext = EGL10.EGL_NO_CONTEXT;
    private EGLSurface mEGLSurface = EGL10.EGL_NO_SURFACE;

    // OpenGL...
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
                    "uniform mat4 transform; \n" +
                    "out vec2 f_texPos; \n" +
                    "void main() { \n" +
                    "   gl_Position = transform * vec4(pos, 1); \n" +
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
    private int mTransformMatrixHandle;

    private int[] mTex = new int[2];
    private int mTexture1Handle;
    private int mTexture2Handle;
    private int VAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_offscreen);
        mImage = findViewById(R.id.image);
        mWidth = 512;
        mHeight = 512;

        mVertexBuf = GLUtil.array2FloatBuffer(VERTEX_ARRAY);
        new GLThread().start();
    }

    /**
     * 子线程离屏绘制，绘制
     */
    private class GLThread extends Thread {
        @Override
        public void run() {
            createEGL();
            makeCurrent();
            drawFrame();
            swap();
            setBitmap();
            destroyEGL();
        }
    }


    /**
     * 创建EGL环境
     * Call in sub-thread
     */
    private void createEGL() {
        // create EGLDisplay
        mEgl = ((EGL10) EGLContext.getEGL());
        mEGLDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new IllegalStateException("failed to create EGLDisplay");
        }
        int[] version = new int[2];
        mEgl.eglInitialize(mEGLDisplay, version);

        // create EGLConfig
        int[] attributes = new int[]   {
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,// very important!
                EGL10.EGL_SURFACE_TYPE,EGL10.EGL_PBUFFER_BIT,//EGL_WINDOW_BIT EGL_PBUFFER_BIT we will create a pixelbuffer surface
                EGL10.EGL_RED_SIZE,   8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE,  8,
                EGL10.EGL_ALPHA_SIZE, 8,// if you need the alpha channel
                EGL10.EGL_DEPTH_SIZE, 8,// if you need the depth buffer
                EGL10.EGL_STENCIL_SIZE,8,
                EGL10.EGL_NONE
        };
        int[] configNum = new int[1];
        mEgl.eglChooseConfig(mEGLDisplay, attributes, null, 0, configNum);
        if (configNum[0] <= 0) {
            // no suitable config , exception
            throw new IllegalStateException("failed to create EGLConfig");
        }
        EGLConfig[] configs = new EGLConfig[configNum[0]];
        mEgl.eglChooseConfig(mEGLDisplay, attributes, configs, configNum.length, configNum);
        mEGLConfig = configs[0];

        // create EGLContext
        int attrs[] = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE, };
        mEGLContext = mEgl.eglCreateContext(mEGLDisplay, mEGLConfig, EGL10.EGL_NO_CONTEXT, attrs);
        if (mEGLContext == EGL10.EGL_NO_CONTEXT) {
            throw new IllegalStateException("failed to created EGLContext");
        }

        // create EGLSurface - 创建一个离屏的EGLSurface，设置其宽高
        int[] surfaceAttrs = new int[]{
                EGL10.EGL_WIDTH, mWidth,
                EGL10.EGL_HEIGHT, mHeight,
                EGL10.EGL_NONE
        };
        mEGLSurface = mEgl.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, surfaceAttrs);
        if (mEGLSurface == EGL10.EGL_NO_SURFACE) {
            throw new IllegalStateException("failed to created EGLSurface");
        }
    }

    /**
     * 设置当前线程为渲染环境
     */
    private void makeCurrent() {
        if (!mEgl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext) ){
            throw new IllegalArgumentException("make current failed.");
        }
    }

    private void drawFrame() {
        GLES30.glClearColor(0, 1, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        mProgram = GLUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (mProgram < 0) {
            throw new IllegalStateException("create program error");
        }
        mPosHandle = GLES30.glGetAttribLocation(mProgram, "pos");
        mTexPosHandle = GLES30.glGetAttribLocation(mProgram, "texPos");
        mTransformMatrixHandle = GLES30.glGetUniformLocation(mProgram, "transform");
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

        GLES30.glViewport(0, 0, mWidth, mHeight);
        // 画出来......................
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
        Matrix.rotateM(matrix, 0, 90, 0, 0, 1);
//        Matrix.translateM(matrix, 0, -1f, -1f, 0);
        GLES30.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, matrix, 0);
    }

    /**
     * 上屏
     */
    private void swap() {
        mEgl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

    private void setBitmap() {
        IntBuffer RGBABuffer = IntBuffer.allocate(mWidth * mHeight);
        RGBABuffer.position(0);
        GLES30.glReadPixels(0, 0, mWidth, mHeight,GLES30.GL_RGBA,GLES30.GL_UNSIGNED_BYTE,RGBABuffer);
        int[] pixls = RGBABuffer.array();
        for (int y = 0; y < mHeight / 2; y++) {
            for (int x = 0; x < mWidth; x++) {
                int pos1 = y * mWidth + x;
                int pos2 = (mHeight - 1 - y) * mWidth + x;
                int tmp = pixls[pos1];
                pixls[pos1] = (pixls[pos2] & 0xFF00FF00) | ((pixls[pos2] >> 16) & 0xff) | ((pixls[pos2] << 16) & 0x00ff0000); // ABGR->ARGB
                pixls[pos2] = (tmp & 0xFF00FF00) | ((tmp >> 16) & 0xff) | ((tmp << 16) & 0x00ff0000);
            }
        }
        if (mHeight % 2 == 1) { // 中间一行
            for (int x = 0; x < mWidth; x++) {
                int pos = (mHeight / 2 + 1) * mWidth + x;
                pixls[pos] = (pixls[pos] & 0xFF00FF00) | ((pixls[pos] >> 16) & 0xff) | ((pixls[pos] << 16) & 0x00ff0000);
            }
        }
        final Bitmap modelBitmap = Bitmap.createBitmap(pixls, mWidth, mHeight, Bitmap.Config.ARGB_8888);
    }

    /**
     * 销毁EGL环境
     */
    private void destroyEGL() {
        mEgl.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
        mEgl.eglDestroySurface(mEGLDisplay, mEGLSurface);
        mEgl.eglTerminate(mEGLDisplay);

        mEgl = null;
        mEGLDisplay = EGL10.EGL_NO_DISPLAY;
        mEGLSurface = EGL10.EGL_NO_SURFACE;
        mEGLContext = EGL10.EGL_NO_CONTEXT;
    }
}
