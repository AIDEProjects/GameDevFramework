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

public abstract class GLGameView extends GLSurfaceView
{
	public enum TAG {
		LifeCycle
	}
	
	public static class CFG {
		public boolean enableViewportGesture = false;
	}
	private CFG cfg;
	
	private GestureHandler gestureHandler;

	private GLRenderer renderer;
	public GLRenderer Renderer() { return renderer; }

	public abstract void create();
	public abstract void render();


	public GLGameView(Context ctx, CFG cfg) {
		super(ctx);
		init(ctx, cfg);
	}

	private void init(Context ctx, CFG cfg) {
		setEGLContextClientVersion(2);

		if(cfg == null){
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
		

		// 初始化手势管理
		GestureHandler.GestureListener listener = new GestureHandler.GestureListener() {
			@Override
			public void onDoublePointerMove(float dx, float dy) {
				renderer.Camera().translate(dx, dy);
			}

			@Override
			public void onScale(float scale) {
				renderer.Camera().scale(scale);
			}
		};
		gestureHandler = new GestureHandler(listener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(cfg.enableViewportGesture) 
			return gestureHandler.handleTouchEvent(event, getWidth(), getHeight());
		return false;
	}


	public class GLRenderer implements GLSurfaceView.Renderer
	{
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

			Log.logfT(TAG.LifeCycle, "onSurfaceChanged布局视口更新: vpWidth: %s, vpHeight: %s", width, height);
			
			if (!initialized) return;
			
			camera.updateViewport(width, height);
		}

	}

}
