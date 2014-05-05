package com.camera.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

/**
 * һ�����ڽ����Ĺ�����
 * 
 * ��ֻ��ץȡ��̬view���������ڲ�����Ƶ������surfaceview�����߶�̬��ֽ�ȵȣ�ץ�����Ϊ������������Ϊsurfaceview��ͬ��view��
 * �ײ�ʵ���ǲ�һ����
 * SurfaceView��View��ʵ��������ڣ�surfaceView����һ������ĵ����߳��п������»��ƻ����View������UI�����߳��и��»���
 * 
 */
public class ScreenShot {
	public static Bitmap takeScreenShot(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();// ��ȡ״̬���߶�
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;// ��ȡ��Ļ���͸�
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();// ȥ��������
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

	public static void savePic(Bitmap b, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public static void shoot(Activity a, File file) {
		if (file == null) {
			return;
		}
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		savePic(ScreenShot.takeScreenShot(a), file);
	}
}
