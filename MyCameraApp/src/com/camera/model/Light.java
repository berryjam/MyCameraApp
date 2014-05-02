package com.camera.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.camera.control.renderer.MyGLRenderer;

import android.opengl.GLES20;

public class Light {
	private final int mProgram;
	private int mPositionHandle;

	private final String vertexShaderCode = "attribute vec4 vPosition;"
			+ "void main() {" + "  gl_Position = vPosition;" + "}";

	private final String fragmentShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	private FloatBuffer vertexBuffer;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static float[] dotCoords = new float[] { 0.0f, 0.0f, 0.0f };
	float color[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	public Light() {
		vertexBuffer = ByteBuffer.allocateDirect(dotCoords.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(dotCoords);
		vertexBuffer.position(0);

		int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderCode);

		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);
	}

	public void draw(float[] mvpMatrix) {
		GLES20.glUseProgram(mProgram);

		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		GLES20.glEnableVertexAttribArray(mPositionHandle);

		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, 4, vertexBuffer);
	}
}
