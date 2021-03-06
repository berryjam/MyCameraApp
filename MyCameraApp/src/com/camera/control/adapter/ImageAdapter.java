package com.camera.control.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * ������������������������-- ��������?��������������������������?
 * 
 * @author Administrator
 * 
 */

public class ImageAdapter extends BaseAdapter {

	private int[] images; // ��������������?
	private Context context;

	public ImageAdapter(Context context, int[] images) {
		super();
		this.context = context;
		this.images = images;
	}

	// ��������?
	public int getCount() {

		return images.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {

		return position;
	}

	// ���������������iew
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView = new ImageView(context); // ������������������ImageView��������?
		imageView.setImageResource(images[position]); // ��������������������������������������������������
		imageView.setScaleType(ImageView.ScaleType.FIT_XY); // ��������������������������������?
		imageView.setLayoutParams(new Gallery.LayoutParams(136, 88));

		return imageView;
	}

}
