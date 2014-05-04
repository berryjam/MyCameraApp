/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camera.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.camera.control.renderer.MyGLRenderer;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {
	private static final String TAG = "MyGLSurfaceView";

	private final MyGLRenderer mRenderer;

	public MyGLSurfaceView(Context context) {
		super(context);

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		setZOrderOnTop(true);

		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new MyGLRenderer();
		mRenderer.setModelPath(Environment.getExternalStorageDirectory()
				.toString() + "/Pictures/MyCameraApp/heartcenter.stl");
		setRenderer(mRenderer);
		// setRenderer(renderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		setZOrderOnTop(true);

		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new MyGLRenderer();
		mRenderer.setModelPath(Environment.getExternalStorageDirectory()
				.toString() + "/Pictures/MyCameraApp/heartcenter.stl");

		setRenderer(mRenderer);
		// setRenderer(renderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;

	private boolean translateFlag = false;
	private boolean scaleFlag = false;
	private boolean rotateFlag = false;

	private int mode;

	private float previousDistance;

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = 1;
			Log.v(TAG, "MotionEvent.ACTION_DOWN" + " mode:" + mode);
			break;
		case MotionEvent.ACTION_UP:
			mode = 0;
			Log.v(TAG, "MotionEvent.ACTION_UP" + " mode:" + mode);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode -= 1;
			if (scaleFlag) {
				if (mode == 1) {
					float newDistance = spacing(e);
					if (newDistance > previousDistance + 1
							|| newDistance < previousDistance - 1) {
						float scaleRatio = newDistance / previousDistance;
						mRenderer.setScaleRatio(scaleRatio);
						previousDistance = newDistance;
						Log.v(TAG, "scaleRatio:" + scaleRatio);
					}
				}
			}
			Log.v(TAG, "MotionEvent.ACTION_POINTER_UP" + " mode:" + mode);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode += 1;
			if (mode == 2)
				previousDistance = spacing(e);
			Log.v(TAG, "MotionEvent.ACTION_POINTER_DOWN" + " mode:" + mode);
			break;
		case MotionEvent.ACTION_MOVE:
			if (rotateFlag) {
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;

				// reverse direction of rotation above the mid-line
				if (y > getHeight() / 2) {
					dx = dx * -1;
				}

				// reverse direction of rotation to left of the mid-line
				if (x < getWidth() / 2) {
					dy = dy * -1;
				}

				mRenderer.setAngle(mRenderer.getAngle()
						+ ((dx + dy) * TOUCH_SCALE_FACTOR)); // = 180.0f /
																// 320
			}
			if (translateFlag) {
				//FIXME 平移操作有问题，需要认真设计
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				mRenderer.setTranslate(dx, dy);
				Log.v(TAG, "mRenderer.setTranslate(" + dx + "," + dy + ");");
			}
			Log.v(TAG, "MotionEvent.ACTION_MOVE" + " mode:" + mode);
			break;
		}
		requestRender();
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	// 交替设置平移标识
	public void setTranslateFlag() {
		if (translateFlag)
			translateFlag = false;
		else {
			translateFlag = true;
			scaleFlag = false;
			rotateFlag = false;
		}
	}

	// 交替设置放缩标识
	public void setScaleFlag() {
		if (scaleFlag)
			scaleFlag = false;
		else {
			scaleFlag = true;
			translateFlag = false;
			rotateFlag = false;
		}
	}

	// 交替设置旋转标识
	public void setRotateFlag() {
		if (rotateFlag)
			rotateFlag = false;
		else {
			rotateFlag = true;
			translateFlag = false;
			scaleFlag = false;
		}
	}

	public boolean getTranslateFlag() {
		return this.translateFlag;
	}

	public boolean getScaleFlag() {
		return this.scaleFlag;
	}

	public boolean getRotateFlag() {
		return this.rotateFlag;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}
}
