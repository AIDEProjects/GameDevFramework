package com.goldsprite.gamedevframework;

import android.opengl.*;

public class ShaderUtils
{

	private static final String VERTEX_SHADER_CODE =
	"uniform mat4 u_Matrix;" +
	"attribute vec4 a_Position;" +
	"void main() {" +
	"  gl_Position = u_Matrix * a_Position;" +
	"}";

	private static final String FRAGMENT_SHADER_CODE =
	"precision mediump float;" +
	"uniform vec4 u_Color;" +
	"void main() {" +
	"  gl_FragColor = u_Color;" +
	"}";

	public static int createProgram() {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

		int program = GLES20.glCreateProgram();
		GLES20.glAttachShader(program, vertexShader);
		GLES20.glAttachShader(program, fragmentShader);
		GLES20.glLinkProgram(program);

		int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] == 0) {
			GLES20.glDeleteProgram(program);
			throw new RuntimeException("Error creating program: " + GLES20.glGetProgramInfoLog(program));
		}

		return program;
	}

	private static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if (compileStatus[0] == 0) {
			GLES20.glDeleteShader(shader);
			throw new RuntimeException("Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
		}

		return shader;
	}
}


