package com.test.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * 鍥剧墖閫傞厤鍣�-- 缁撳�?鐢诲粖杩涜浣跨�?
 * 
 * @author Administrator
 * 
 */

public class ImageAdapter extends BaseAdapter {

	private int[] images; // 鏁版嵁婧�?
	private Context context;

	public ImageAdapter(Context context, int[] images) {
		super();
		this.context = context;
		this.images = images;
	}

	// 鏁伴�?
	public int getCount() {

		return images.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {

		return position;
	}

	// 鏄剧ず鐨刅iew
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView = new ImageView(context); // 鍒涘缓涓�釜ImageView瑙嗗�?
		imageView.setImageResource(images[position]); // 涓哄綋鍓嶅璞¤缃浘鐗囪祫婧�
		imageView.setScaleType(ImageView.ScaleType.FIT_XY); // 鍥剧墖鐨勫竷灞�柟寮�?
		imageView.setLayoutParams(new Gallery.LayoutParams(136, 88));

		return imageView;
	}

}
