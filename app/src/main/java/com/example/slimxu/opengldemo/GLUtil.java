package com.example.slimxu.opengldemo;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by slimxu on 2018/9/29.
 */

public class GLUtil {

    public static int createProgram(String vertexShader, String fragmentShader) {
        int vertexShaderId = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        int fragmentShaderId = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(vertexShaderId, vertexShader);
        GLES30.glShaderSource(fragmentShaderId, fragmentShader);
        GLES30.glCompileShader(vertexShaderId);
        GLES30.glCompileShader(fragmentShaderId);

        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShaderId);
        GLES30.glAttachShader(program, fragmentShaderId);
        GLES30.glLinkProgram(program);

        int[] status = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES30.GL_TRUE) {
            Log.e("GLUtil", "can not link program, vertexShader error : " +   GLES30.glGetShaderInfoLog(vertexShaderId));
            Log.e("GLUtil", "can not link program, fragmentShader error : " + GLES30.glGetShaderInfoLog(fragmentShaderId));
            GLES30.glDeleteProgram(program);
            program = -1;
        }

        return program;
    }

    public static FloatBuffer array2FloatBuffer(float[] array) {
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(array.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(array);
        floatBuffer.position(0);
        return floatBuffer;
    }



}
