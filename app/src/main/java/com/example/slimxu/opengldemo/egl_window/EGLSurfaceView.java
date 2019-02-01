package com.example.slimxu.opengldemo.egl_window;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.EGL14;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.slimxu.opengldemo.GLUtil;
import com.example.slimxu.opengldemo.R;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import static com.example.slimxu.opengldemo.GLUtil.array2FloatBuffer;

/**
 * 在SurfaceView手动创建EGL环境
 * 进行上屏绘制
 */
public class EGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // Handler message
    private static final int MSG_SURFACE_CREATED = 1;
    private static final int MSG_SURFACE_CHANGED = 2;
    private static final int MSG_SURFACE_DESTROYED = 3;
    private static final int MSG_UPDATE_FRAME = 4;

    // SurfaceHolder & HandlerTread
    private SurfaceHolder mHolder;
    private HandlerThread mHandlerThread;
    private Handler mSubThreadHandler;

    private int mWidth;
    private int mHeight;

    // EGL...
    private EGL10 mEgl;
    private EGLDisplay mEGLDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext = EGL10.EGL_NO_CONTEXT;
    private EGLSurface mEGLSurface = EGL10.EGL_NO_SURFACE;

    // OpenGL Draw
    private final float[] VERTEX_ARRAY = {
            // 位置顶点    // 纹理顶点
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

    private FloatBuffer mVertexBuf;

    private final String VERTEX_SHADER =
            "#version 300 es \n" +
                    "layout (location = 0) in vec3 pos; \n" +
                    "layout (location = 1) in vec2 texPos; \n" +
                    "uniform mat4 transform; \n" +
                    "uniform mat4 model; \n" +
                    "uniform mat4 view; \n" +
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
    private int mTransformMatrixHandle;
    private int mModelMatrixHandle;;
    private int mViewMatrixHandle;
    private int mProjectionHandle;

    private int[] mTex = new int[2];
    private int mTexture1Handle;
    private int mTexture2Handle;
    private int VAO;

    private float[] mModelMatrix = new float[16];   // 模型矩阵
    private float[] mViewMatrix = new float[16];    // 观察矩阵
    private float[] mProjectionMatrix = new float[16];    // 投影矩阵

    // 模型旋转的角度
    private float mRotateValue = 0f;

    public EGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public EGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 创建渲染线程
        mHandlerThread = new HandlerThread("draw_thread");
        mHandlerThread.start();
        mSubThreadHandler = new Handler(mHandlerThread.getLooper(), new GLTreadCallback());

        mHolder = getHolder();
        mHolder.addCallback(this);

        mVertexBuf = array2FloatBuffer(VERTEX_ARRAY);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSubThreadHandler.sendEmptyMessage(MSG_SURFACE_CREATED);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
        mSubThreadHandler.sendEmptyMessage(MSG_SURFACE_CHANGED);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSubThreadHandler.sendEmptyMessage(MSG_SURFACE_DESTROYED);
    }

    private class GLTreadCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SURFACE_CREATED:
                    createEGL();
                    makeCurrent();
                    onSurfaceCreateGL();
                    guardedRun();
                    break;
                case MSG_SURFACE_CHANGED:
                    guardedRun();
                    break;
                case MSG_UPDATE_FRAME:
                    mRotateValue += 5f;
                    drawFrame();
                    swap();
                    mSubThreadHandler.sendEmptyMessageDelayed(MSG_UPDATE_FRAME, 16);
                    break;
                case MSG_SURFACE_DESTROYED:
                    mSubThreadHandler.removeCallbacksAndMessages(null);
                    destroyEGL();
                    break;
            }
            return true;
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
        int[] attributes = new int[] {
                EGL10.EGL_RED_SIZE, 8,  //指定RGB中的R大小（bits）
                EGL10.EGL_GREEN_SIZE, 8, //指定G大小
                EGL10.EGL_BLUE_SIZE, 8,  //指定B大小
                EGL10.EGL_ALPHA_SIZE, 8, //指定Alpha大小，以上四项实际上指定了像素格式
                EGL10.EGL_DEPTH_SIZE, 16, //指定深度缓存(Z Buffer)大小
                EGL10.EGL_RENDERABLE_TYPE, 4, //指定渲染api类别, 如上一小节描述，这里或者是硬编码的4，或者是EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE };  //总是以EGL10.EGL_NONE结尾
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

        // create EGLSurface - 创建一个上屏的EGLSurface，用的就是SurfaceHolder作为承载内容的容器
        mEGLSurface = mEgl.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, mHolder, null);
        if (mEGLSurface == EGL10.EGL_NO_SURFACE) {
            throw new IllegalStateException("failed to created EGLSurface");
        }
    }

    /**
     * 设置当前线程为渲染环境
     */
    private void makeCurrent() {
        mEgl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
    }

    /**
     * 上屏
     */
    private void swap() {
        mEgl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

    /**
     * 在onSurfaceCreated回调中执行OpenGL操作：create program，获取pointer..
     */
    private void onSurfaceCreateGL() {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        mProgram = GLUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (mProgram < 0) {
            throw new IllegalStateException("create program error");
        }
        mPosHandle = GLES30.glGetAttribLocation(mProgram, "pos");
        mTexPosHandle = GLES30.glGetAttribLocation(mProgram, "texPos");
        mTransformMatrixHandle = GLES30.glGetUniformLocation(mProgram, "transform");
        mModelMatrixHandle = GLES30.glGetUniformLocation(mProgram, "model");
        mViewMatrixHandle = GLES30.glGetUniformLocation(mProgram, "view");
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

    public void drawFrame() {

        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

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
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glBindVertexArray(0);


        // 设置模型矩阵
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setRotateM(mModelMatrix, 0, mRotateValue, 0.5f, 1f, 0);
        GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);

        // 设置观察矩阵
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, 0, 0, -5);    // 将整个场景往Z负轴移动，这样我们就能观察到了
        GLES30.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);

        // 设置投影矩阵
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, (float)mWidth / mHeight, 0.1f, 100f);
        GLES30.glUniformMatrix4fv(mProjectionHandle, 1, false, mProjectionMatrix, 0);


        // uniform Matrix
//        float[] matrix = new float[16];
//        Matrix.setIdentityM(matrix, 0);
//        GLES30.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, matrix, 0);

        /**
         * 你可能注意到纹理上下颠倒了！这是因为OpenGL要求y轴0.0坐标是在图片的底部的，但是图片的y轴0.0坐标通常在顶部。
         */
    }

    /**
     * 仿照GLSurfaceView来一个无限循环绘制
     */
    private void guardedRun() {
        mSubThreadHandler.removeCallbacksAndMessages(null);
        mSubThreadHandler.sendEmptyMessage(MSG_UPDATE_FRAME);
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
