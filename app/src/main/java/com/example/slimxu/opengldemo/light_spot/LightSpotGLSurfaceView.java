package com.example.slimxu.opengldemo.light_spot;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.slimxu.opengldemo.GLCamera;
import com.example.slimxu.opengldemo.GLUtil;
import com.example.slimxu.opengldemo.R;
import com.example.slimxu.opengldemo.Vector;


import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LightSpotGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private final float VERTEX_ARRAY[] = {
            // 顶点位置             // 法向量位置        // 漫反射贴图坐标
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,

            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,

            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f
    };
    private FloatBuffer mVertexBuffer;

    private final Vector[] CUBE_POSITIONS = {
            new Vector(0.0f,  0.0f,  0.0f),
            new Vector( 2.0f,  5.0f, -15.0f),
            new Vector(-1.5f, -2.2f, -2.5f),
            new Vector(-3.8f, -2.0f, -12.3f),
            new Vector(2.4f, -0.4f, -3.5f),
            new Vector(-1.7f,  3.0f, -7.5f),
            new Vector(1.3f, -2.0f, -2.5f),
            new Vector(1.5f,  2.0f, -2.5f),
            new Vector(1.5f,  0.2f, -1.5f),
            new Vector(-1.3f,  1.0f, -1.5f)
    } ;


    private final String VERTEX_SHADER =
            "#version 300 es \n" +
            "uniform mat4 model; \n" +
            "uniform mat4 view; \n" +
            "uniform mat4 projection; \n" +
            "layout (location = 0) in vec3 pos; \n" +
            "layout (location = 1) in vec3 normal;  // 该顶点的法向量，立方体的法向量我们传进来\n" +
            "layout (location = 2) in vec2 texPos;  // 光照贴图纹理坐标 \n" +
            "out vec3 Normal;   // 所有的光照计算都在FS中进行，所以传给FS\n" +
            "out vec3 FragPos;  // 片段的位置（世界坐标系，通过将Pos乘以Model矩阵得到） \n" +
            "out vec3 ViewPos; \n" +
            "out vec2 TexPos;  \n" +
            "void main() { \n" +
            "   gl_Position = projection * view * model * vec4(pos, 1.0f); \n" +
            "   Normal = mat3(transpose(inverse(model))) * normal; \n" +
            "   FragPos = vec3(model * vec4(pos, 1.0f)); \n" +
            "   TexPos = texPos; \n" +
            "} \n";
    private final String FRAGMENT_SHADER =
            "#version 300 es \n" +
            "precision mediump float; \n" +
            // 物体材质
            "struct Material { \n" +
            "   sampler2D diffuse; \n" +
            "   sampler2D specular; \n" +
            "   sampler2D emission; // 放射光贴图，新增，发光值是(模拟)物体自身发光(Emit)时可能产生的颜色\n" +
            "   float shininess; \n" +
            "}; \n" +
            //
            "struct Light { \n" +
                // 聚光光源需要四个要素：位置、方向、切光角、外层切光角。做（平滑/软化边缘）
            "   vec3 position; \n" +
            "   vec3 direction; \n" +
            "   float cutOff;   \n" +
            "   float outCutOff; \n" +

            "   vec3 ambient; \n" +
            "   vec3 diffuse; \n" +
            "   vec3 specular; \n" +
            //  用于计算衰减的常量
            "   float constant; // 常数项 \n" +
            "   float linear;   // 一次项 \n" +
            "   float quadratic; // 二次项 \n" +
            "}; \n" +

            "in vec3 Normal; // 法向量\n" +
            "in vec3 FragPos; // 片段位置（世界坐标系）; \n" +
            "in vec2 TexPos;  // 光照贴图纹理坐标 \n" +
            "uniform Material material; // 物体材质 \n" +
            "uniform Light light; \n" +
            "uniform vec3 viewPos; // 观察者的位置，传入摄像机位置 \n" +
            "out vec4 color; \n" +
            "void main() { \n" +
            "   float theta = dot(normalize(FragPos - light.position), normalize(light.direction)); \n" +   // 当前角度的cos值
            "   float value = (theta - light.outCutOff) / (light.cutOff - light.outCutOff); \n" +   // 让在 cutOff 与 outCutOff之间的光亮度，平滑渐变 由1.0 ~ 0.0
            "   float intensity = clamp(value, 0.0f, 1.0f); \n" +

            "   vec3 materialDiffuse = vec3(texture(material.diffuse, TexPos)); // 贴图颜色就为物体颜色\n" +
            "   vec3 materialSpecular = vec3(texture(material.specular, TexPos)); //  镜面高光颜色 \n" +

            "   vec3 ambient = light.ambient * materialDiffuse;    // 环境光照    \n" +

            "   vec3 norm = normalize(Normal); \n" +
            "   vec3 lightDir = normalize(light.position - FragPos); \n" +
            "   float diff = max(dot(norm, lightDir), 0.0f); // 散射因子 \n" +
            "   vec3 diffuse = light.diffuse * materialDiffuse * diff;    // 散射光照 \n" +

            "   vec3 viewDir = normalize(viewPos - FragPos); // 观察向量\n" +
            "   vec3 reflectDir = reflect(-lightDir, norm);  // 反射向量\n" +
            "   float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess); \n" +
            "   vec3 specular = light.specular * materialSpecular * spec; // 镜面光照\n" +

            "   vec3 emission = vec3(texture(material.emission, TexPos)); \n" +

            //  计算衰减值
            "   float distance = length(light.position - FragPos); // 光源到片段的距离 \n" +
            "   float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * distance * distance); \n" +

//            "   ambient *= attenuation; \n" +     // 衰减 和 亮度渐变值，都不影响环境色，不然就一片漆黑了
            "   diffuse = diffuse * attenuation * intensity; \n" +
            "   specular = specular * attenuation * intensity; \n" +

            "   vec3 finalColor = ambient + diffuse + specular /*+ emission*/; \n" +
            "   color = vec4(finalColor , 1.0f); \n" +
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
            "precision mediump float; \n" +
            "out vec4 color; \n" +
            "void main() { \n" +
            "   color = vec4(1.0f); \n" +
            "}  \n";


    private int mProgram;
    private int mLightProgram;

    private int mObjVAO;
    private int mLightVAO;
    private int mVBO;

    public GLCamera mCamera = new GLCamera(new Vector(0, 0, 6));
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    private int mWidth;
    private int mHeight;

    private float mLastTouchX;
    private float mLastTouchY;

    private Vector mObjectPos = new Vector(0f, 0f, 0f);
    private Vector mLightPos = new Vector(0.4f, 0.6f, 2.0f);
    private Vector mLightAmbientColor = new Vector(0.2f, 0.2f, 0.2f);   // 默认光对环境色的影响
    private Vector mLightDiffuseColor = new Vector(0.5f, 0.5f, 0.5f);   // 默认光对漫反射的影响
    private Vector mLightSpeclarColor = new Vector(1, 1, 1);   // 默认光对漫反射的影响

    private int[] mTextures;
    private int mFuckHandle;

    private long mStartTime;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            float durTime =  (System.currentTimeMillis() - mStartTime) * 0.001f;
//            mLightColor.x = (float) Math.sin(durTime * 2);
//            mLightColor.y = (float) Math.sin(durTime * 0.7f);
//            mLightColor.z = (float) Math.sin(durTime * 1.3f);
//
//            mLightDiffuseColor.x = mLightColor.x * 0.5f;
//            mLightDiffuseColor.y = mLightColor.y * 0.5f;
//            mLightDiffuseColor.z = mLightColor.z * 0.5f;
//
//            mLightAmbientColor.x = mLightDiffuseColor.x * 0.2f;
//            mLightAmbientColor.y = mLightDiffuseColor.y * 0.2f;
//            mLightAmbientColor.z = mLightDiffuseColor.z * 0.2f;

            requestRender();
            mHandler.sendEmptyMessageDelayed(1, 16);
        }
    };

    public LightSpotGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public LightSpotGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mStartTime = System.currentTimeMillis();
        mVertexBuffer = GLUtil.array2FloatBuffer(VERTEX_ARRAY);
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mHandler.sendEmptyMessage(1);

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
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mProgram, "pos"), 3, GLES30.GL_FLOAT, false, 8 * 4, 0);
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mProgram, "normal"), 3, GLES30.GL_FLOAT, false, 8 * 4, 3 * 4);
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mProgram, "texPos"), 2, GLES30.GL_FLOAT, false, 8 * 4, 6 * 4);
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mProgram, "pos"));
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mProgram, "normal"));
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mProgram, "texPos"));
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);

        GLES30.glBindVertexArray(mLightVAO);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO);
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(mLightProgram, "pos"), 3, GLES30.GL_FLOAT, false, 8 * 4, 0);
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(mLightProgram, "pos"));
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);

        // gen diffuse specular emission texture
        mTextures = new int[3];
        GLES30.glGenTextures(3, mTextures, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(getResources(), R.drawable.container2), 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[1]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(getResources(), R.drawable.container2_specular), 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[2]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(getResources(), R.drawable.matrix), 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
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
        int unifViewPos = GLES30.glGetUniformLocation(mProgram, "viewPos");

        // view
        GLES30.glUniformMatrix4fv(unifViewPointer, 1, false, mCamera.getViewMatrix(), 0);

        // projection
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.zoom, (float)mWidth / mHeight, 0.1f, 100f);
        GLES30.glUniformMatrix4fv(unifProjectionPointer, 1, false, mProjectionMatrix, 0);

        // viewPos
        Vector viewPos = mCamera.getPosition();
        GLES30.glUniform3f(unifViewPos, viewPos.x, viewPos.y, viewPos.z);

        // 物体材质
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "material.shininess"), 64.0f);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[0]);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram, "material.diffuse"), 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[1]);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram, "material.specular"), 1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[2]);
        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram, "material.emission"), 2);

        // 光源特性（光的颜色，环境、漫反射、镜面反射）
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "light.ambient"),
                mLightAmbientColor.x, mLightAmbientColor.y, mLightAmbientColor.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "light.diffuse"),
                mLightDiffuseColor.x, mLightDiffuseColor.y, mLightDiffuseColor.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "light.specular"),
                mLightSpeclarColor.x, mLightSpeclarColor.y, mLightSpeclarColor.z);

        // 光源特性（聚光的位置，方向，和切光角）
        Vector cameraPos = mCamera.getPosition();
        Vector front = mCamera.getFront();
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "light.position"),
                cameraPos.x, cameraPos.y, cameraPos.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "light.direction"),
                front.x, front.y, front.z);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "light.cutOff"),
                (float) Math.cos(Math.toRadians(5)));
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "light.outCutOff"),
                (float) Math.cos(Math.toRadians(10)));

        // 光源特性（衰减的3个常量）
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "light.constant "), 1.0f);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "light.linear"), 0.045f);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "light.quadratic"), 0.0075f);


        GLES30.glBindVertexArray(mObjVAO);
        for (int i = 0;i < CUBE_POSITIONS.length; i++) {
            // model
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, CUBE_POSITIONS[i].x, CUBE_POSITIONS[i].y, CUBE_POSITIONS[i].z);
            Matrix.rotateM(mModelMatrix, 0, i * 20f, 1f, 0.3f, 0.5f);
            GLES30.glUniformMatrix4fv(unifModelPointer, 1, false, mModelMatrix, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        }
        GLES30.glBindVertexArray(0);

        // 手电筒光源，第一人称视角，不用点光源
//        GLES30.glUseProgram(mLightProgram);
//
//        unifModelPointer = GLES30.glGetUniformLocation(mLightProgram, "model");
//        unifViewPointer = GLES30.glGetUniformLocation(mLightProgram, "view");
//        unifProjectionPointer = GLES30.glGetUniformLocation(mLightProgram, "projection");
//
//        // model
//        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.translateM(mModelMatrix, 0, mLightPos.x, mLightPos.y, mLightPos.z);
//        Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
//        GLES30.glUniformMatrix4fv(unifModelPointer, 1, false, mModelMatrix, 0);
//
//        // view
//        GLES30.glUniformMatrix4fv(unifViewPointer, 1, false, mCamera.getViewMatrix(), 0);
//
//        // projection
//        Matrix.setIdentityM(mProjectionMatrix, 0);
//        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.zoom, (float)mWidth / mHeight, 0.1f, 100f);
//        GLES30.glUniformMatrix4fv(unifProjectionPointer, 1, false, mProjectionMatrix, 0);
//
//
//        GLES30.glBindVertexArray(mLightVAO);
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
//        GLES30.glBindVertexArray(0);

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

    public void up() {
        mCamera.processKeyboardMovement(GLCamera.Direction.UP, 0.5f);
        requestRender();
    }

    public void down() {
        mCamera.processKeyboardMovement(GLCamera.Direction.DOWN, 0.5f);
        requestRender();
    }

    private void checkProgram() {
        GLUtil.checkProgram(mProgram);
        GLUtil.checkProgram(mLightProgram);
    }
}
