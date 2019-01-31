package com.example.slimxu.opengldemo.egl_window;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.GLES30;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * 在SurfaceView手动创建EGL环境
 * 进行上屏绘制
 */
public class EGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int MSG_SURFACE_CREATED = 1;
    private static final int MSG_SURFACE_CHANGED = 2;
    private static final int MSG_SURFACE_DESTROYED = 3;

    private SurfaceHolder mHolder;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private int mWidth;
    private int mHeight;

    // EGL...
    private EGL10 mEgl;
    private EGLDisplay mEGLDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext = EGL10.EGL_NO_CONTEXT;
    private EGLSurface mEGLSurface = EGL10.EGL_NO_SURFACE;

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
        mHandler = new Handler(mHandlerThread.getLooper(), new GLTreadCallback());

        mHolder = getHolder();
        mHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHandler.sendEmptyMessage(MSG_SURFACE_CREATED);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
        mHandler.sendEmptyMessage(MSG_SURFACE_CHANGED);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHandler.sendEmptyMessage(MSG_SURFACE_DESTROYED);
    }

    private class GLTreadCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SURFACE_CREATED:
                    createEGL();
                    makeCurrent();
                    guardedRun();
                    break;
                case MSG_SURFACE_CHANGED:
                    break;
                case MSG_SURFACE_DESTROYED:
                    destroyEGL();
                    break;
            }
            return true;
        }
    }

    /**
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

    private void makeCurrent() {
        // 设置当前线程为渲染环境
        mEgl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
    }

    // 仿照GLSurfaceView来一个无限循环绘制
    private void guardedRun() {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glClearColor(1, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glViewport(0,0, mWidth, mHeight);

        // 上屏
        mEgl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

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
