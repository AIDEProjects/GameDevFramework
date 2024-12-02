package com.goldsprite.gamedevframework;

import android.content.*;
import android.opengl.*;
import android.view.*;
import com.goldsprite.appdevframework.math.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;

import javax.microedition.khronos.egl.EGLConfig;
import android.util.*;
import com.goldsprite.appdevframework.log.*;

public abstract class GLGameView extends GLSurfaceView
{
    private GestureHandler gestureHandler;

	private GLRenderer renderer;
	public GLRenderer Renderer(){ return renderer; }

	public abstract void create();
	public abstract void render();


	public GLGameView(Context ctx) {
		super(ctx);
		init(ctx);
	}
	public GLGameView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx);
	}

	private void init(Context ctx) {
		setEGLContextClientVersion(2);

		// 初始化渲染器
		renderer = new GLRenderer();
		setRenderer(renderer);

		// 初始化手势管理
		GestureHandler.GestureListener listener = new GestureHandler.GestureListener() {
			@Override
			public void onSinglePointerMove(float dx, float dy) {
				//renderer.onMoveCamera(dx, dy);
				//renderer.translate(dx, dy);
			}

			@Override
			public void onDoublePointerMove(float dx, float dy) {
				//renderer.onMoveCamera(dx, dy);
				renderer.translate(dx, dy);
			}

			@Override
			public void onScale(float scale) {
				//renderer.onScaleCamera(scale);
				renderer.scale(scale);
			}
		};
		gestureHandler = new GestureHandler(listener);
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureHandler.handleTouchEvent(event, getWidth(), getHeight());
        return true;
    }
	
	
	public class GLRenderer implements GLSurfaceView.Renderer
	{
		private final float[] projectionMatrix = new float[16];
		private final float[] viewMatrix = new float[16];
		private final float[] vpMatrix = new float[16];
		public float[] VpMatrix() { return vpMatrix; }

		private float scale = 1.0f;
		private Vector2 translate = new Vector2().set(0f);
		private Vector2 sclTranslateOffset = new Vector2();

		private Vector2 viewportSize = new Vector2();
		public Vector2 getViewportSize() {
			viewportSize.set(getWidth(), getHeight());
			return viewportSize;
		}

		public Vector2 SclTranslate() {
			sclTranslateOffset.set(translate.clone().scl(scale * 2.5f));
			return sclTranslateOffset;
		}


		@Override
		public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
			GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1f);
			create();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);

			float ratio = (float) width / height;
			Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
		}

		@Override
		public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
			// 清空屏幕
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

			// 摄像机位移与缩放实现
			Matrix.setIdentityM(viewMatrix, 0);
			Matrix.translateM(viewMatrix, 0, translate.x + SclTranslate().x, translate.y + SclTranslate().y, 0);
			Matrix.scaleM(viewMatrix, 0, scale, scale, 1);
			Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

			render();
		}


		public void translate(float dx, float dy) {
			float scl = Math.max(1, scale);
			translate.add(dx / scl, dy / scl);
		}

		public void scale(float factor) {
			scale *= factor;
		}
	}

}
