package com.goldsprite.gamedevframework;
import android.opengl.*;
import com.goldsprite.appdevframework.log.*;

public class GLDebug
{
	public static void checkGLError(String tag) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			String errMsg = tag+" OpenGLError: id: "+error+", cause: " + GLUtils.getEGLErrorString(error);
			Log.log(errMsg);
			throw new RuntimeException(errMsg);
		}
	}
}
