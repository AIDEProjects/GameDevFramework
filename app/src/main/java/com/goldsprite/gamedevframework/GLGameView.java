package com.goldsprite.gamedevframework;

import android.content.*;
import android.opengl.*;
import android.view.*;
import com.goldsprite.appdevframework.math.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;

import javax.microedition.khronos.egl.EGLConfig;
import android.util.*;
import com.goldsprite.appdevframework.log.Log;
import com.goldsprite.appdevframework.log.*;
import com.goldsprite.appdevframework.apputils.GestureHandler;

public class GLGameView extends GLSurfaceView {

	public enum TAG {
		LifeCycle
		}
	static {
		Log.showTags.put(GLGameView.TAG.LifeCycle, Log.LogMode.encodeMode(true, true));
	}

	public static class CFG {
		public boolean enableViewportGesture = false;
	}
	private CFG cfg;
	public CFG Cfg() { return cfg; }

	private GestureHandler gestureHandler;
	public GestureHandler GestureHandler() { return gestureHandler; }

	private GestureHandler.GestureListener listener;
	public GestureHandler.GestureListener GestureListener() { return listener; }

	private Vector2Int 
	stageSize = new Vector2Int(), 
	viewportSize = new Vector2Int(), 
	coordSign = new Vector2Int();

	private GLRenderer renderer;
	public GLRenderer Renderer() { return renderer; }

	public void create() {}
	public void render() {}


	public GLGameView(Context ctx, CFG cfg) {
		super(ctx);
		init(ctx, cfg);
	}

	private void init(Context ctx, CFG cfg) {
		setEGLContextClientVersion(2);

		if (cfg == null) {
			cfg = new CFG();
		}
		this.cfg = cfg;

		// 初始化渲染器
		renderer = new GLRenderer();
		setRenderer(renderer);

		Runnable initRun = new Runnable(){
			public void run() {
				String str = "GLGameView视图已准备";
				Log.logT(TAG.LifeCycle, str);
			}
		};
		post(initRun);

		listener = new GestureHandler.GestureListener(){
			public boolean hasView() {
				return true;
			}
			public Vector2Int getStageSize() {
				View child0 = GLGameView.this;
				stageSize.set(child0.getWidth(), child0.getHeight());
				return stageSize;
			}
			public Vector2Int getViewportSize() {
				viewportSize.set(getWidth(), getHeight());
				return viewportSize;
			}
			public Vector2Int coordinatesSigned(){
				coordSign.set(1, 1);
				return coordSign;
			}
			
			public void onDoublePointerMove(float x, float y) {
				renderer.Camera().translation(x, y);
			}
			public void onScale(float setScale) {
				renderer.Camera().setScale(setScale);
			}
		};
		final GestureHandler.CFG gestureCfg = new GestureHandler.CFG();
		gestureCfg.allSet(cfg.enableViewportGesture);
		gestureHandler = new GestureHandler(listener, gestureCfg);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureHandler.handleTouchEvent(event);
	}


	public class GLRenderer implements GLSurfaceView.Renderer {
		private boolean initialized;

		private OrthoCamera camera;
		public OrthoCamera Camera() { return camera; }


		public void init() {
			camera = new OrthoCamera(getWidth(), getHeight());
			create();
			initialized = true;
			Log.logT(TAG.LifeCycle, "游戏视图GLRenderer渲染器已完成初始化initialized");
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1f);
			Log.logT(TAG.LifeCycle, "onSurfaceCreated视图已创建");
			init();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			// 清空屏幕
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

			if (!initialized) return;

			//Log.logT(TAG.LifeCycle, "onDrawFrame帧绘制循环");

			// 摄像机位移与缩放实现
			camera.updateMatrix();

			render();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);

			Log.logT(TAG.LifeCycle, "onSurfaceChanged布局视口更新: vpWidth: %s, vpHeight: %s", width, height);

			if (!initialized) return;

			camera.updateViewport(width, height);
		}

	}

}
