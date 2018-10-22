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
            // 顶点位置             // 法向量位置
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,

            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
    };
    private FloatBuffer mVertexBuffer;

    private final String VERTEX_SHADER =
            "#version 300 es \n" +
            "uniform mat4 model; \n" +
            "uniform mat4 view; \n" +
            "uniform mat4 projection; \n" +
            "layout (location = 0) in vec3 pos; \n" +
            "layout (location = 1) in vec3 normal;  // 该顶点的法向量，立方体的法向量我们传进来\n" +
            "out vec3 Normal;   // 所有的光照计算都在FS中进行，所以传给FS\n" +
            "out vec3 FragPos;  // 片段的位置（世界坐标系，通过将Pos乘以Model矩阵得到） \n" +
            "out vec3 ViewPos; \n" +
            "void main() { \n" +
            "   gl_Position = projection * view * model * vec4(pos, 1.0f); \n" +
            "   Normal = mat3(transpose(inverse(model))) * normal; \n" +
            "   FragPos = vec3(model * vec4(pos, 1.0f)); \n" +
            "} \n";
    private final String FRAGMENT_SHADER =
            "#version 300 es \n" +
            "precision mediump float; \n" +
            "in vec3 Normal; // 法向量\n" +
            "in vec3 FragPos; // 片段位置（世界坐标系）; \n" +
            "uniform vec3 objectColor; \n" +
            "uniform vec3 lightColor; \n" +
            "uniform vec3 lightPos; // 光源的位置（世界坐标系）\n" +
            "uniform vec3 viewPos; // 观察者的位置，传入摄像机位置 \n" +
            "out vec4 color; \n" +
            "void main() { \n" +
            "   float ambientStrength = 0.1f;   // 环境光照因子\n" +
            "   vec3 ambient = lightColor * ambientStrength;    // 环境光照    \n" +

            "   vec3 norm = normalize(Normal); \n" +
            "   vec3 lightDir = normalize(lightPos - FragPos); \n" +
            "   float diff = max(dot(norm, lightDir), 0.0f); // 散射因子 \n" +
            "   vec3 diffuse = lightColor * diff;    // 散射光照 \n" +

            "   float specularStrength = 0.5f;  // 镜面强度 \n" +
            "   vec3 viewDir = normalize(viewPos - FragPos); // 观察向量\n" +
            "   vec3 reflectDir = reflect(-lightDir, norm);  // 反射向量\n" +
            "   float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);  // 镜面亮度分量， 32是物体的发光值，发光值越高，反射光的能力越强\n" +
            "   vec3 specular = specularStrength * spec * lightColor; // 镜面光照\n" +

            "   vec3 finalLightColor = ambient + diffuse + specular; \n" +
            "   color = vec4(objectColor * finalLightColor  , 1.0f); \n" +
            "} \n";


    private final String LIGHT_VERTEX_SHADER =
            "#version 300 es \n" +
            "uniform mat4 model; \n" +
            "uniform mat4 view; \n" +
            "uniform mat4 projection; \n" +
            "layout (location = 0) in vec3 pos; \n" +
            "void main() { \n" +
            "   gl_Position = projection * view * model * vec4(pos, 1.0f); \n" +
            "} \n";
    private final String LIGHT_FRAGMENT_SHADER =
            "#version 300 es \n" +
            "out vec4 color; \n" +
            "void main() { \n" +
            "   color = vec4(1.0f); \n" +
            "}  \n";


    private int mProgram;
    private int mLightProgram;

    private int mObjVAO;
    private int mLightVAO;
    private int mVBO;

    public GLCamera mCamera = new GLCamera(new Vector(0, 0, 7));
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    private int mWidth;
    private int mHeight;

    private float mLastTouchX;
    private float mLastTouchY;

    private Vector mObjectPos = new Vector(0f, 0f, 0f);
    private Vector mLightPos = new Vector(1f, 0f, 3f);

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
        mLightProgram = GLUtil.createProgram(LIGHT_VERTEX_SHADER, LIGHT_FRAGMENT_SHADER);

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
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mProgram, "pos"), 3, GLES30.GL_FLOAT, false, 6 * 4, 0);
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mProgram, "normal"), 3, GLES30.GL_FLOAT, false, 6 * 4, 3 * 4);
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mProgram, "pos"));
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mProgram, "normal"));
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);

        GLES30.glBindVertexArray(mLightVAO);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO);
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mLightProgram, "pos"), 3, GLES30.GL_FLOAT, false, 6 * 4, 0);
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mLightProgram, "pos"));
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);
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

        //  用mProgram画物体
        GLES30.glUseProgram(mProgram);

        int unifModelPointer = GLES30.glGetUniformLocation(mProgram, "model");
        int unifViewPointer = GLES30.glGetUniformLocation(mProgram, "view");
        int unifProjectionPointer = GLES30.glGetUniformLocation(mProgram, "projection");
        int unifObjectColor = GLES30.glGetUniformLocation(mProgram, "objectColor");
        int unifLightColor = GLES30.glGetUniformLocation(mProgram, "lightColor");
        int unifLightPos = GLES30.glGetUniformLocation(mProgram, "lightPos");
        int unifViewPos = GLES30.glGetUniformLocation(mProgram, "viewPos");

        // model
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mObjectPos.x, mObjectPos.y, mObjectPos.z);
        Matrix.scaleM(mModelMatrix, 0, 0.9f, 0.9f, 0.9f);
        Matrix.rotateM(mModelMatrix, 0, -45, 0, 1, 0);
        Matrix.rotateM(mModelMatrix, 0, 45, 1, 0, 0);
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

        // lightPos
        GLES30.glUniform3f(unifLightPos, mLightPos.x, mLightPos.y, mLightPos.z);

        // viewPos
        Vector viewPos = mCamera.getPosition();
        GLES30.glUniform3f(unifViewPos, viewPos.x, viewPos.y, viewPos.z);

        GLES30.glBindVertexArray(mObjVAO);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glBindVertexArray(0);

        // 切换到lightProgram画光源
        GLES30.glUseProgram(mLightProgram);

        unifModelPointer = GLES30.glGetUniformLocation(mLightProgram, "model");
        unifViewPointer = GLES30.glGetUniformLocation(mLightProgram, "view");
        unifProjectionPointer = GLES30.glGetUniformLocation(mLightProgram, "projection");

        // model
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mLightPos.x, mLightPos.y, mLightPos.z);
        Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
        Matrix.rotateM(mModelMatrix, 0, 30, 0, 1, 0);
        Matrix.rotateM(mModelMatrix, 0, 30, 1, 0, 0);
        GLES30.glUniformMatrix4fv(unifModelPointer, 1, false, mModelMatrix, 0);

        // view
        GLES30.glUniformMatrix4fv(unifViewPointer, 1, false, mCamera.getViewMatrix(), 0);

        // projection
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.zoom, (float)mWidth / mHeight, 0.1f, 100f);
        GLES30.glUniformMatrix4fv(unifProjectionPointer, 1, false, mProjectionMatrix, 0);

        GLES30.glBindVertexArray(mLightVAO);
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
        Assert.assertTrue(mLightProgram != -1);
    }
}
