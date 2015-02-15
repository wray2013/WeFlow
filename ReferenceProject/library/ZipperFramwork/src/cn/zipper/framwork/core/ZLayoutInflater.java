package cn.zipper.framwork.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class ZLayoutInflater {
	
	private static final LayoutInflater inflater = ZSystemService.getLayoutInflater();
	
	private ZLayoutInflater() {
	}
	
	public static ZViewFinder inflateInZViewFinder(int id) {
		return new ZViewFinder(inflate(id));
	}
	
	public static ZViewFinder inflateInZViewFinder(int id, ViewGroup root) {
		return new ZViewFinder(inflate(id, root));
	}
	
	public static View inflate(int id) {
		return inflate(id, null);
	}
	
	public static View inflate(int id, ViewGroup root) {
		return inflater.inflate(id, root);
	}

}
