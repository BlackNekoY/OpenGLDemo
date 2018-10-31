package com.example.slimxu.opengldemo;

/**
 * 向量
 * Created by slimxu on 2018/10/18.
 */

public class Vector {
    public float x;
    public float y;
    public float z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    /**
     * 俩向量的叉乘
     */
    public static Vector cross(Vector a, Vector b) {
        float x = a.y * b.z - a.z * b.y;
        float y = a.z * b.x - a.x * b.z;
        float z = a.x * b.y - a.y * b.x;

        return new Vector(x, y, z);
    }

    /**
     * 标准化向量
     */
    public static Vector normalize(Vector v) {
        double length = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);   // 向量的模
        return new Vector(v.x / (float)length, v.y / (float)length, v.z / (float)length);
    }

//
//    public Vector multiply(float value) {
//        Vector vector = new Vector(this);
//        vector.x *= value;
//        vector.y *= value;
//        vector.z *= value;
//        return vector;
//    }
//
//    public Vector add(Vector other) {
//        Vector vector = new Vector(this);
//        vector.x += other.x;
//        vector.y += other.y;
//        vector.z += other.z;
//        return vector;
//    }

}
