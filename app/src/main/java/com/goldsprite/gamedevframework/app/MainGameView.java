package com.goldsprite.gamedevframework.app;

import android.content.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.gamedevframework.*;
import com.goldsprite.appdevframework.log.*;
import com.goldsprite.appdevframework.log.Debug;

public class MainGameView implements GLGameView.IGameCycleListener {
	protected GLGameView core;
	protected GLGameView.GLRenderer renderer;

	protected ShapeBatch batch;
	protected OrthoCamera camera;
	protected float lineWidth = 800;
	protected Vector2 vCenter, vSize;

	public void create0() {}
	public void render0() {}
	public void viewportChanged0(float width, float height) {}


	public GLGameView initView(Context ctx) { return initView(ctx, null); }
	public GLGameView initView(Context ctx, GLGameView.CFG cfg) {

		core = new GLGameView(ctx, cfg, this);

		return core;
	}

	public final void create() {
		renderer = core.Renderer();
		camera = renderer.Camera();

		batch = new ShapeBatch(core);

		vSize = camera.getViewportSize();
		vCenter = vSize.clone().div(2);

		create0();
	}
	public final void render() {
		batch.setColor(Color.deepBlue);
		batch.drawStrokeRect(vCenter.x, vCenter.y, vSize.x, vSize.y, lineWidth, Align.CENTER);

		render0();
	}
	public final void viewportChanged(float width, float height) {
		vSize.set(width, height);
		vCenter.set(vSize.x / 2f, vSize.y / 2f);
		viewportChanged0(width, height);
	}

}
