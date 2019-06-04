package com.example.arcore;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = MainRenderer.class.getSimpleName();

    private boolean mViewportChanged;
    private int mViewportWidth;
    private int mViewportHeight;

    private CameraRenderer mCamera;
    private PlaneRenderer mPlane;

    private Cube mCube;

    private RenderCallback mRenderCallback;

    public interface RenderCallback {
        void preRender();
    }

    public MainRenderer(RenderCallback callback, float width, float depth, float height) {
        /*float width = 0.3f;
        float depth = 0.3f;
        float height = 0.3f;*/


        mCamera = new CameraRenderer();

        mPlane = new PlaneRenderer(Color.GRAY, 0.5f);

        mCube = new Cube(width, depth, height, Color.BLUE, 0.5f);

        mRenderCallback = callback;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);

        mCamera.init();

        mPlane.init();

        mCube.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mViewportChanged = true;
        mViewportWidth = width;
        mViewportHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderCallback.preRender();

        GLES20.glDepthMask(false);
        mCamera.draw();
        GLES20.glDepthMask(true);

        //mPlane.draw();

        mCube.draw();
    }

    public int getTextureId() {
        return mCamera == null ? -1 : mCamera.getTextureId();
    }

    public void onDisplayChanged() {
        mViewportChanged = true;
    }

    public boolean isViewportChanged() {
        return mViewportChanged;
    }

    public void updateSession(Session session, int displayRotation) {
        if (mViewportChanged) {
            session.setDisplayGeometry(displayRotation, mViewportWidth, mViewportHeight);
            mViewportChanged = false;
        }
    }

    public void transformDisplayGeometry(Frame frame) {
        mCamera.transformDisplayGeometry(frame);
    }


    public void updatePlane(Plane plane) {
        mPlane.update(plane);
    }


    public void setCubeModelMatrix(float[] matrix) {
        mCube.setModelMatrix(matrix);
    }

    public void setProjectionMatrix(float[] matrix) {
        mPlane.setProjectionMatrix(matrix);
        mCube.setProjectionMatrix(matrix);
    }

    public void updateViewMatrix(float[] matrix) {
        mPlane.setViewMatrix(matrix);
        mCube.setViewMatrix(matrix);
    }
}
