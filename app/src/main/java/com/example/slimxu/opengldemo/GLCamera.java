package com.example.slimxu.opengldemo;


import android.opengl.Matrix;

public class GLCamera {

    public enum Direction {
        FORWARD, BACKWARD, LEFT, RIGHT
    }

    private static final float YAW = -90;   // X轴偏航角
    private static final float PITCH = 0;   // Y轴俯仰角
    private static final float ZOOM = 45;   // 视野
    private static final float SENSITIVTY = 0.05f;

    private float yaw;
    private float pitch;
    public float zoom;
    private float sensitivty;

    // 摄像机的4个关键向量
    private Vector position;
    private Vector front;
    private Vector up;
    private Vector right;

    /**
     * 世界坐标系的up，计算right向量用
     * 默认的世界up为(0, 1, 0)
     */
    private Vector worldUp = new Vector(0, 1, 0);

    private float[] viewMatrix = new float[16];

    public GLCamera(Vector position, Vector up, Vector front,
                    float sensitivty, float zoom,
                    float yaw, float pitch) {

        this.position = position;
        this.up = up;
        this.front = front;
        this.sensitivty = sensitivty;
        this.zoom = zoom;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public GLCamera(Vector position) {
        this(position, new Vector(0, 1, 0), new Vector(0, 0, -1), SENSITIVTY, ZOOM,
                YAW, PITCH);
    }

    public float[] getViewMatrix() {
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                position.x, position.y, position.z,
                position.x + front.x, position.y + front.y, position.z + front.z,
                up.x, up.y, up.z);
        return viewMatrix;
    }

    /**
     * 处理手指移动
     */
    public void processPointerMovement(float xOffset, float yOffset) {
        xOffset *= sensitivty;
        yOffset *= sensitivty;

        yaw += xOffset;
        pitch += yOffset;
        if (pitch > 89) {
            pitch = 89;
        }
        if (pitch < -89) {
            pitch = -89;
        }

        updateCameraVectors();
    }

    /**
     * 处理上下左右移动,朝着front方向修改Position
     */
    public void processKeyboardMovement(Direction direction, float offset) {
        if (direction == Direction.FORWARD) {
            position.x += front.x * offset;
            position.y += front.y * offset;
            position.z += front.z * offset;
        } else if(direction == Direction.BACKWARD) {
            position.x -= front.x * offset;
            position.y -= front.y * offset;
            position.z -= front.z * offset;
        } else if(direction == Direction.LEFT) {
            Vector crossVector = Vector.cross(front, up);
            position.x -= crossVector.x * offset;
            position.y -= crossVector.y * offset;
            position.z -= crossVector.z * offset;
        } else if(direction == Direction.RIGHT) {
            Vector crossVector = Vector.cross(front, up);
            position.x += crossVector.x * offset;
            position.y += crossVector.y * offset;
            position.z += crossVector.z * offset;
        }
    }

    /**
     * 更新摄像机的三个向量，front,up,right
     */
    private void updateCameraVectors() {
        float yawRadians = (float) Math.toRadians(yaw);
        float pitchRadians = (float) Math.toRadians(pitch);
        front.x = (float) (Math.cos(yawRadians) * Math.cos(pitchRadians));
        front.y = (float) Math.sin(pitchRadians);
        front.z = (float) (Math.sin(yawRadians) * Math.cos(pitchRadians));
        right = Vector.cross(front, worldUp);
        up = Vector.cross(right, front);
    }
}
