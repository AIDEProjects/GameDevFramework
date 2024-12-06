package com.goldsprite.gamedevframework.app;
import com.goldsprite.appdevframework.apputils.*;
import com.goldsprite.gamedevframework.*;
import android.content.*;
import android.view.*;
import android.app.*;

public class DebugGameViewBuilder extends DebugActivityLayoutBuilder
{
	public DebugGameViewBuilder(Activity ctx, MainGameView game){ this(ctx, game, null); }
	public DebugGameViewBuilder(Activity ctx, MainGameView game, GLGameView.CFG cfg){
		super(ctx, game.initView(ctx, cfg));
	}
}
