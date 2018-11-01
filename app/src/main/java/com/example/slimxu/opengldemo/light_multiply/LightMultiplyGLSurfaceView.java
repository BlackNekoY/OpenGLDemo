package com.example.slimxu.opengldemo.light_multiply;

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

import junit.framework.Assert;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LightMultiplyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

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

    private final Vector[] POINT_LIGHT_POSITIONS = {
            new Vector( 0.7f,  0.2f,  2.0f),
            new Vector( 2.3f, -3.3f, -4.0f),
            new Vector(-4.0f,  2.0f, -12.0f),
            new Vector( 0.0f,  0.0f, -3.0f)
    };
    private final Vector[] POINT_LIGHT_AMBIENT = {
            new Vector(0.2f, 0.2f, 0.2f),
            new Vector(0.2f, 0.2f, 0.2f),
            new Vector(0.2f, 0.2f, 0.2f),
            new Vector(0.2f, 0.2f, 0.2f)
    };
    private final Vector[] POINT_LIGHT_DIFFUSE = {
            new Vector(1f, 0.2f, 0.2f),
            new Vector(0.2f, 1f, 0.2f),
            new Vector(0.2f, 0.2f, 1f),
            new Vector(0.2f, 0.2f, 0.2f)
    };
    private final Vector[] POINT_LIGHT_SPECULAR = {
            new Vector(0.2f, 0.2f, 0.2f),
            new Vector(0.2f, 0.2f, 0.2f),
            new Vector(0.2f, 0.2f, 0.2f),
            new Vector(0.2f, 0.2f, 0.2f)
    };

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
            "   sampler2D diffuse; // 物体纹理贴图\n" +
            "   sampler2D specular; // 物体镜面反射贴图 \n" +
            "   float shininess;    // 物体发光值\n" +
            "}; \n" +

            // 手电筒
            "struct FlashLight { \n" +
            "   vec3 position; \n" +
            "   vec3 direction; \n" +
            "   float cutOff;   \n" +
            "   float outCutOff; \n" +

            "   vec3 ambient; \n" +
            "   vec3 diffuse; \n" +
            "   vec3 specular; \n" +

            "   float constant; // 常数项 \n" +
            "   float linear;   // 一次项 \n" +
            "   float quadratic; // 二次项 \n" +
            "}; \n" +

            // 点光源
            "struct PointLight { \n" +
            "   vec3 position; \n" +

            "   vec3 ambient; \n" +
            "   vec3 diffuse; \n" +
            "   vec3 specular; \n" +

            "   float constant; // 常数项 \n" +
            "   float linear;   // 一次项 \n" +
            "   float quadratic; // 二次项 \n" +
            "}; \n" +

            // 定向光
            "struct DirLight { \n" +
            "   vec3 direction; \n" +

            "   vec3 ambient; \n" +
            "   vec3 diffuse; \n" +
            "   vec3 specular; \n" +

            "}; \n" +

            "uniform vec3 viewPos; // 观察者的位置，传入摄像机位置(世界坐标系) \n" +
            "uniform Material material; // 物体材质 \n" +
            "uniform DirLight dirLight; \n" +
            "uniform FlashLight flashLight; \n" +
            "#define POINT_LIGHT_COUNT 4 \n" +
            "uniform PointLight pointLights[POINT_LIGHT_COUNT]; \n" +

            "in vec3 Normal; // 法向量\n" +
            "in vec3 FragPos; // 片段位置（世界坐标系）; \n" +
            "in vec2 TexPos;  // 光照贴图纹理坐标 \n" +

            "out vec4 color; \n" +

            // 函数声明(不知道为啥不可以声明 吃屎吧)
//            "vec3 calculateDirLight(DirLight dirLight, vec3 normal, vec3 viewDir, Material material); \n" +
//            "vec3 calculatePointLight(PointLight pointLight, Material material); \n" +
//            "vec3 calculateFlashLight(FlashLight flashLight, Material material); \n" +

            // 函数定义
            "vec3 calculateDirLight(DirLight dirLight, vec3 normal, vec3 viewDir, Material material) { \n" +
            "   vec3 ambient = dirLight.ambient; \n" +
            "   vec3 lightDir = normalize(dirLight.direction); \n" +
            "   float diffuseValue = max(dot(-lightDir, normal), 0.0f); \n" +
            "   vec3 diffuse = dirLight.diffuse * diffuseValue; \n" +
            "   float specularValue = pow(max(dot(reflect(lightDir, normal), viewDir), 0.0f), material.shininess); \n" +
            "   vec3 specular = dirLight.specular * specularValue; \n" +
            "   vec3 materialDiffuse = vec3(texture(material.diffuse, TexPos)); \n" +
            "   vec3 materialSpecular = vec3(texture(material.specular, TexPos)); \n" +
            "   return ambient * materialDiffuse + diffuse * materialDiffuse + specular * materialSpecular; \n" +
            "}\n" +

            "vec3 calculateFlashLight(FlashLight flashLight, vec3 normal, vec3 viewDir, vec3 fragPos, Material material) { \n" +
            "   vec3 lightDir = normalize(fragPos - flashLight.position); \n" +
            "   vec3 ambient = flashLight.ambient; \n" +
            "   float diffuseValue = max(dot(-lightDir, normal), 0.0f); \n" +
            "   vec3 diffuse = flashLight.diffuse * diffuseValue; \n" +
            "   float specularValue = pow(max(dot(reflect(lightDir, normal), viewDir), 0.0f), material.shininess); \n" +
            "   vec3 specular = flashLight.specular * specularValue; \n" +

            "   float theta = max(dot(lightDir, flashLight.direction), 0.0f); \n" +
            "   float gradientValue = clamp((theta - flashLight.outCutOff) / (flashLight.cutOff - flashLight.outCutOff), 0.0f, 1.0f); \n" +
            "   float distance = length(fragPos - flashLight.position); \n" +
            "   float attenuationValue = (1.0f / (flashLight.constant + flashLight.linear * distance + flashLight.quadratic * distance * distance)); \n" +

            "   diffuse = diffuse * gradientValue * attenuationValue; \n" +
            "   specular = specular * gradientValue * attenuationValue; \n" +

            "   vec3 materialDiffuse = vec3(texture(material.diffuse, TexPos)); \n" +
            "   vec3 materialSpecular = vec3(texture(material.specular, TexPos)); \n" +
            "   return ambient * materialDiffuse + diffuse * materialDiffuse + specular * materialSpecular; \n" +
            "} \n" +

            "vec3 calculatePointLight(PointLight pointLight, vec3 normal, vec3 viewDir, vec3 fragPos, Material material) { \n" +
                "   vec3 lightDir = normalize(fragPos - pointLight.position); \n" +
                "   vec3 ambient = pointLight.ambient; \n" +
                "   float diffuseValue = max(dot(-lightDir, normal), 0.0f); \n" +
                "   vec3 diffuse = pointLight.diffuse * diffuseValue; \n" +
                "   float specularValue = pow(max(dot(reflect(lightDir, normal), viewDir), 0.0f), material.shininess); \n" +
                "   vec3 specular = pointLight.specular * specularValue; \n" +

                "   float distance = length(fragPos - pointLight.position); \n" +
                "   float attenuationValue = (1.0f / (pointLight.constant + pointLight.linear * distance + pointLight.quadratic * distance * distance)); \n" +

                "   diffuse = diffuse * attenuationValue; \n" +
                "   specular = specular * attenuationValue; \n" +

                "   vec3 materialDiffuse = vec3(texture(material.diffuse, TexPos)); \n" +
                "   vec3 materialSpecular = vec3(texture(material.specular, TexPos)); \n" +
                "   return ambient * materialDiffuse + diffuse * materialDiffuse + specular * materialSpecular; \n" +
            "} \n" +

            "void main() { \n" +
            "   vec3 normal = normalize(Normal); \n" +
            "   vec3 viewDir = normalize(viewPos - FragPos); \n" +
            "   vec3 finalColor; \n" +
            "   finalColor = calculateDirLight(dirLight, normal, viewDir, material); \n" +
            "   for (int i = 0;i < POINT_LIGHT_COUNT;i++) { \n" +
            "       finalColor += calculatePointLight(pointLights[i], normal, viewDir, FragPos, material); \n" +
            "   } \n" +
            "   finalColor += calculateFlashLight(flashLight, normal, viewDir, FragPos, material); \n" +
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

    // 定向光
    private Vector mDirectLightDirection = new Vector(-0.2f, -1.0f, -0.3f);
    private Vector mDirectLightAmbient = new Vector(0.2f, 0.2f, 0.2f);
    private Vector mDirectLightDiffuse = new Vector(0.5f, 0.5f, 0.5f);
    private Vector mDirectLightSpecular = new Vector(1, 1, 1);

    private Vector mFlashLightAmbient = new Vector(0.2f, 0.2f, 0.2f);
    private Vector mFlashLightDiffuse = new Vector(0.5f, 0.5f, 0.5f);
    private Vector mFlashLightSpecular = new Vector(1, 1, 1);


    private int[] mTextures;
    private int mFuckHandle;

    private long mStartTime;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            requestRender();
            mHandler.sendEmptyMessageDelayed(1, 16);
        }
    };

    public LightMultiplyGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public LightMultiplyGLSurfaceView(Context context, AttributeSet attrs) {
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
        mTextures = new int[2];
        GLES30.glGenTextures(2, mTextures, 0);
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


        // 定向光
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "dirLight.direction"),
                mDirectLightDirection.x, mDirectLightDirection.y, mDirectLightDirection.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "dirLight.ambient"),
                mDirectLightAmbient.x, mDirectLightAmbient.y, mDirectLightAmbient.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "dirLight.diffuse"),
                mDirectLightDiffuse.x, mDirectLightDiffuse.y, mDirectLightDiffuse.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "dirLight.specular"),
                mDirectLightSpecular.x, mDirectLightSpecular.y, mDirectLightSpecular.z);

        // 手电筒
        Vector cameraPos = mCamera.getPosition();
        Vector cameraFront = mCamera.getFront();
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "flashLight.position"),
                cameraPos.x, cameraPos.y, cameraPos.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "flashLight.direction"),
                cameraFront.x, cameraFront.y, cameraFront.z);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "flashLight.cutOff"),
                (float) Math.cos(Math.toRadians(5)));
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "flashLight.outCutOff"),
                (float) Math.cos(Math.toRadians(10)));
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "flashLight.ambient"),
                mFlashLightAmbient.x, mFlashLightAmbient.y, mFlashLightAmbient.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "flashLight.diffuse"),
                mFlashLightDiffuse.x, mFlashLightDiffuse.y, mFlashLightDiffuse.z);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, "flashLight.specular"),
                mFlashLightSpecular.x, mFlashLightSpecular.y, mFlashLightSpecular.z);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "flashLight.constant "),
                1.0f);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "flashLight.linear"),
                0.045f);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, "flashLight.quadratic"),
                0.0075f);

        // 多个点光源
        for (int i = 0;i < POINT_LIGHT_POSITIONS.length; i++) {
            Vector pos = POINT_LIGHT_POSITIONS[i];
            Vector ambient = POINT_LIGHT_AMBIENT[i];
            Vector diffuse = POINT_LIGHT_DIFFUSE[i];
            Vector specular = POINT_LIGHT_SPECULAR[i];

            String lightName = "pointLights[" + i + "]"; // flashLight[0]

            GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, lightName + ".position"),
                    pos.x, pos.y, pos.z);
            GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, lightName + ".ambient"),
                    ambient.x, ambient.y, ambient.z);
            GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, lightName + ".diffuse"),
                    diffuse.x, diffuse.y, diffuse.z);
            GLES30.glUniform3f(GLES30.glGetUniformLocation(mProgram, lightName + ".specular"),
                    specular.x, specular.y, specular.z);
            GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, lightName + ".constant "),
                    1.0f);
            GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, lightName + ".linear"),
                    0.045f);
            GLES30.glUniform1f(GLES30.glGetUniformLocation(mProgram, lightName + ".quadratic"),
                    0.0075f);
        }

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

        // 画点光源
        GLES30.glUseProgram(mLightProgram);

        unifModelPointer = GLES30.glGetUniformLocation(mLightProgram, "model");
        unifViewPointer = GLES30.glGetUniformLocation(mLightProgram, "view");
        unifProjectionPointer = GLES30.glGetUniformLocation(mLightProgram, "projection");

        // view
        GLES30.glUniformMatrix4fv(unifViewPointer, 1, false, mCamera.getViewMatrix(), 0);

        // projection
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.zoom, (float)mWidth / mHeight, 0.1f, 100f);
        GLES30.glUniformMatrix4fv(unifProjectionPointer, 1, false, mProjectionMatrix, 0);


        GLES30.glBindVertexArray(mLightVAO);
        for (int i = 0;i < POINT_LIGHT_POSITIONS.length;i++) {
            Vector pos = POINT_LIGHT_POSITIONS[i];

            // model
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, pos.x, pos.y, pos.z);
            Matrix.scaleM(mModelMatrix, 0, 0.2f, 0.2f, 0.2f);
            GLES30.glUniformMatrix4fv(unifModelPointer, 1, false, mModelMatrix, 0);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        }
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

    public void up() {
        mCamera.processKeyboardMovement(GLCamera.Direction.UP, 0.5f);
        requestRender();
    }

    public void down() {
        mCamera.processKeyboardMovement(GLCamera.Direction.DOWN, 0.5f);
        requestRender();
    }

    private void checkProgram() {
        Assert.assertTrue(mProgram != -1);
        Assert.assertTrue(mLightProgram != -1);
    }
}
