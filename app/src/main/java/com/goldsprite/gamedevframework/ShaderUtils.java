package com.goldsprite.gamedevframework;

import android.opengl.*;
import java.util.*;
import java.nio.*;

public class ShaderUtils {

	public enum ShaderMode { Color, Texture }
	
	public static String a_Position = "a_Position";
	public static String u_Matrix = "u_Matrix";
	public static String u_Color = "u_Color";
	public static String a_TexCoord = "a_TexCoord";
	public static String v_TexCoord = "v_TexCoord";
	public static String u_Texture = "u_Texture";
	
    // 颜色模式的顶点和片段着色器代码
    private static final String COLOR_MODE_VERTEX_SHADER = ""
	+ "uniform mat4 "+u_Matrix+";"
	+ "attribute vec4 "+a_Position+";" 
	+ "void main() {" 
	+ "  gl_Position = "+u_Matrix+" * "+a_Position+";" 
	+ "}";

    private static final String COLOR_MODE_FRAGMENT_SHADER = ""
	+ "precision mediump float;" 
	+ "uniform vec4 "+u_Color+";" 
	+ "void main() {" 
	+ "  gl_FragColor = "+u_Color+";" 
	+ "}";

    // 纹理模式的顶点和片段着色器代码
    private static final String TEXTURE_MODE_VERTEX_SHADER = ""
	+ "uniform mat4 "+u_Matrix+";"
	+ "attribute vec4 "+a_Position+";" 
	+ "attribute vec2 "+a_TexCoord+";" 
	+ "varying vec2 "+v_TexCoord+";" 
	+ "void main() {" 
	+ "  gl_Position = "+u_Matrix+" * "+a_Position+";" 
	+ "  "+v_TexCoord+" = "+a_TexCoord+";" 
	+ "}";

    private static final String TEXTURE_MODE_FRAGMENT_SHADER = ""
	+ "precision mediump float;" 
	+ "varying vec2 "+v_TexCoord+";" 
	+ "uniform sampler2D "+u_Texture+";" 
	+ "void main() {" 
	+ "  vec4 texColor = texture2D("+u_Texture+", "+v_TexCoord+");" 
	+ "  gl_FragColor = vec4(texColor.rgb, texColor.a);"
	+ "}";

	public static Map<Enum, Integer> programMap = new HashMap<Enum, Integer>();
	public static Integer getProgram(ShaderMode mode){ return programMap.get(mode); }
	static{
		programMap.put(ShaderUtils.ShaderMode.Color, ShaderUtils.createProgram(ShaderUtils.ShaderMode.Color));
		programMap.put(ShaderUtils.ShaderMode.Texture, ShaderUtils.createProgram(ShaderUtils.ShaderMode.Texture));
	}

    /**
     * 创建着色器程序，根据传入的模式（color或texture）选择不同的着色器
     * @param mode 渲染模式，可以是 "color" 或 "texture"
     * @return 编译并链接后的着色器程序
     */
    public static int createProgram(ShaderMode mode) {
        int vertexShader;
        int fragmentShader;

        // 根据传入的模式选择不同的顶点和片段着色器代码
        if (ShaderMode.Color.equals(mode)) {
			vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, COLOR_MODE_VERTEX_SHADER);
			fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, COLOR_MODE_FRAGMENT_SHADER);
		}
		else if (ShaderMode.Texture.equals(mode)) {
			vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, TEXTURE_MODE_VERTEX_SHADER);
			fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, TEXTURE_MODE_FRAGMENT_SHADER);
		}
		else {
			throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        // 创建着色器程序并链接
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        // 检查程序链接是否成功
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Error creating program: " + GLES20.glGetProgramInfoLog(program));
        }

		programMap.put(mode, program);
        return program;
    }

	public static void useVertexAttrib(int program, int coordDimen, FloatBuffer vertexBuffer) {
		GLES20.glUseProgram(program); // 使用目标程序

		int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
		GLES20.glVertexAttribPointer(positionHandle, coordDimen, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glEnableVertexAttribArray(positionHandle);
	}

	public static void useColorAttrib(int program, float[] color) {
		int colorHandle = GLES20.glGetUniformLocation(program, "u_Color");
		GLES20.glUniform4fv(colorHandle, 1, color, 0);
	}

	public static void useTextureAttrib(int program, int textureId, FloatBuffer texCoordBuffer) {
		int texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
		int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");

		// 设置纹理坐标数据
		GLES20.glEnableVertexAttribArray(texCoordHandle);
		GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

		// 绑定纹理
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(textureHandle, 0);
	}

	public static void applyMatrix(int program, float[] vpMatrix, float x, float y, float width, float height) {
		int matrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");

		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, 0);
		Matrix.scaleM(modelMatrix, 0, width, height, 1);
		Matrix.multiplyMM(modelMatrix, 0, vpMatrix, 0, modelMatrix, 0);

		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0);
	}

	public static void disableColorProgram(int program) {
		int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
		GLES20.glDisableVertexAttribArray(positionHandle);
	}
	
	public static void disableTextureProgram(int program) {
		int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
		GLES20.glDisableVertexAttribArray(positionHandle);

		int texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
		GLES20.glDisableVertexAttribArray(texCoordHandle);
	}


    /**
     * 加载和编译着色器
     * @param type 着色器类型（顶点或片段）
     * @param shaderCode 着色器代码
     * @return 编译后的着色器
     */
    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // 检查编译是否成功
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
        }

        return shader;
    }
}


