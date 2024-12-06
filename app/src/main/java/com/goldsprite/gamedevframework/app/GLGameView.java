package com.goldsprite.gamedevframework.app;

import android.content.*;
import android.graphics.*;
import android.opengl.*;
import android.view.*;
import com.goldsprite.appdevframework.apputils.*;
import com.goldsprite.appdevframework.log.*;
import com.goldsprite.appdevframework.log.Debug;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.gamedevframework.*;
import java.io.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;

import javax.microedition.khronos.egl.EGLConfig;
import android.os.*;
import com.goldsprite.appdevframework.utils.*;

public class GLGameView extends GLSurfaceView {	
	public enum TAG { LifeCycle, RunningTimes, CalcFPS }
	static {
		Log.setTagMode(GLGameView.TAG.LifeCycle, true, true);
		Debug.showTags.put(GLGameView.TAG.RunningTimes, true);
		Log.setTagMode(GLGameView.TAG.RunningTimes, false, false);
		Log.setTagMode(GLGameView.TAG.CalcFPS, false, false);
	}

	private Context ctx;
	public Context Ctx() { return ctx; }
	private CFG cfg;
	public CFG Cfg() { return cfg; }
	public static class CFG {
		public boolean enableViewportGesture = false;
		public boolean gesture_constrainMovement = true;
		public CFG enableViewportGesture(boolean boo) { enableViewportGesture = boo;return this; }
		public CFG enable_gesture_constrainMovement(boolean boo) { gesture_constrainMovement = boo;return this; }
	}

	private GestureHandler gestureHandler;
	public GestureHandler GestureHandler() { return gestureHandler; }
	private GestureHandler.GestureListener gestureListener;
	public GestureHandler.GestureListener GestureListener() { return gestureListener; }

	private GLRenderer renderer;
	public GLRenderer Renderer() { return renderer; }
	private GLGameView.IGameCycleListener gameListener;
	public interface IGameCycleListener {
		public void create();
		public void render();
		public void viewportChanged(float width, float height);
	}


	private Vector2Int 
	stageSize = new Vector2Int(), 
	viewportSize = new Vector2Int(), 
	coordSign = new Vector2Int();


	public GLGameView(Context ctx, CFG cfg, IGameCycleListener gameListener) {
		super(ctx);
		init(ctx, cfg, gameListener);
	}

	public void init(Context ctx, CFG cfg, IGameCycleListener gameListener) {
		setEGLContextClientVersion(2);

		this.ctx = ctx;

		if (cfg == null) cfg = new CFG();
		this.cfg = cfg;

		this.gameListener = gameListener;

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

		gestureListener = new GestureHandler.GestureListener(){
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
			public Vector2Int coordinatesSigned() {
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
		gestureHandler = new GestureHandler(gestureListener, gestureCfg);

		GestureHandler().Cfg().constrainMovement = cfg.gesture_constrainMovement;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureHandler.handleTouchEvent(event);
	}


	public class GLRenderer implements GLSurfaceView.Renderer {
		private boolean initialized;

		private OrthoCamera camera;

		public OrthoCamera Camera() { return camera; }

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1f);
			GLES20.glEnable(GLES20.GL_CULL_FACE);
			GLES20.glCullFace(GLES20.GL_BACK);
			Log.logT(TAG.LifeCycle, "GLES20启用面剔除: GL_CULL_FACE, 背面GL_BACK");
			// 启用混合
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			Log.logT(TAG.LifeCycle, "GLES20启用透明度混合GL_BLEND");
			Log.logT(TAG.LifeCycle, "视图已创建: onSurfaceCreated()");
			init();
		}

		public void init() {
			camera = new OrthoCamera(getWidth(), getHeight());
			Log.logT(TAG.LifeCycle, "执行游戏监听器创建方法: gameListener.create()");
			gameListener.create();
			initialized = true;
			Log.logT(TAG.LifeCycle, "视图初始化已完成: init()");
			lastFpsTimeNanos = lastTimeNanos = startTimeNanos = currentTimeNanos = TimeUtils.nowTimeNanos();
		}

		private long currentTimeNanos = 0;
		private long startTimeNanos = 0;
		private long lastTimeNanos = 0;
		private long runTimeNanos = 0;
		private long deltaTimeNanos = 0;
		private int frameTick=0;
		private int frameCount = 0;
		private long lastFpsTimeNanos = 0;
		private double fps = 0f;
		public double CurrentTime() { return (double)currentTimeNanos / TimeUtils.Second2Nanos; }
		public double StartTime() { return (double)startTimeNanos / TimeUtils.Second2Nanos; }
		public double RunTimes() { return (double)runTimeNanos / TimeUtils.Second2Nanos; }
		public double DeltaTime() { return (double)deltaTimeNanos / TimeUtils.Second2Nanos; }
		public double Fps() { return fps; }
		@Override
		public void onDrawFrame(GL10 gl) {
			if (!initialized) return;

			debugMes();
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			camera.updateMatrix();
			gameListener.render();
		}
		private void debugMes() {
			calculateTime();
			String msg = "渲染帧调用: render()"
				+ String.format("\n\ttotalFrameTick: %s", frameTick++)
				+ String.format("\n\tcurrentTime: %s, 纳秒: %s", TimeUtils.formatTime(currentTimeNanos), currentTimeNanos)
				+ String.format("\n\tstartTime: %s, 纳秒: %s", TimeUtils.formatTime(startTimeNanos), startTimeNanos)
				+ String.format("\n\trunTime: %s, 秒数: %s, 纳秒: %s", TimeUtils.formatSpendTime(runTimeNanos, 1), RunTimes(), runTimeNanos)
				+ String.format("\n\tdeltaTime: %ss", MathUtils.preciNum(DeltaTime()))
				+ String.format("\n\tfps: %ss", MathUtils.preciNum(Fps()))
				;
			Debug.setDebugInfo(TAG.RunningTimes, msg);
			Log.logT(TAG.RunningTimes, msg);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			if (!initialized) return;

			Log.logT(TAG.LifeCycle, "布局视口更新: onSurfaceChanged(), width: %s, height: %s", width, height);
			GLES20.glViewport(0, 0, width, height);
			camera.updateViewport(width, height);
			gameListener.viewportChanged(width, height);
		}

		private void calculateTime() {
			currentTimeNanos = TimeUtils.nowTimeNanos();
			runTimeNanos = currentTimeNanos - startTimeNanos;
			deltaTimeNanos = currentTimeNanos - lastTimeNanos;
			lastTimeNanos = currentTimeNanos;
			frameCount++;
			long spendNanos = currentTimeNanos - lastFpsTimeNanos;
			double spendSeconds = (double)spendNanos / TimeUtils.Second2Nanos;
			Log.logT(
				TAG.CalcFPS, 
				"FPS计算: "
				+ "\n\t当前计数帧数: %s, \n\t当前纳秒: %s, \n\t上次fps截止纳秒: %s, \n\t已用纳秒: %s, \n\t已用秒: %s", 
				frameCount, currentTimeNanos, lastFpsTimeNanos, spendNanos, spendSeconds);
			if (spendSeconds > 1) {
				fps = (double)frameCount / spendSeconds;
				Log.logT(
					TAG.CalcFPS, 
					"FPS计算完成: "
					+ "\n\t秒内帧数计数: %s, \n\tfps: %s", frameCount, fps);
				frameCount = 0;
				lastFpsTimeNanos = currentTimeNanos;
			}
		}

	}

	public int[] loadTexture(String filePath) {
		final int[] textureHandle = new int[1];
		int textureWidth=0, textureHeight=0;
		try {
			GLES20.glGenTextures(1, textureHandle, 0);
			if (textureHandle[0] != 0) {
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
				InputStream is = ctx.getAssets().open(filePath);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				textureWidth = bitmap.getWidth();
				textureHeight = bitmap.getHeight();
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				bitmap.recycle();
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // Unbind
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return new int[]{ textureHandle[0], textureWidth, textureHeight };
	}

}
