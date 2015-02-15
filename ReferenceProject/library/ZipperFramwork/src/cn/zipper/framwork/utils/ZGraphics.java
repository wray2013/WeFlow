package cn.zipper.framwork.utils;

import cn.zipper.framwork.core.ZApplication;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public final class ZGraphics {
	
	private static final String TAG = "ZGraphics";

	private ZGraphics() {
	}
	
	public static void drawNinepath(Canvas canvas, int id, Rect rect) {
		Bitmap bitmap = BitmapFactory.decodeResource(ZApplication.getInstance().getResources(), id);
		drawNinepath(canvas, bitmap, rect);
    }
	
	public static void drawNinepath(Canvas canvas, Bitmap bitmap, Rect rect) {
		if (bitmap != null) {
			 NinePatch patch = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
			 patch.draw(canvas, rect);  
		}
    }
	
	public static BitmapDrawable toBitmapDrawable(Bitmap bitmap) {
		BitmapDrawable drawable = null;
		if (bitmap != null) {
			drawable = new BitmapDrawable(bitmap);
		}
		return drawable;
	}
	
	public static BitmapDrawable toBitmapDrawable(byte[] bytes) {
		BitmapDrawable drawable = null;
		if (bytes != null) {
			drawable = new BitmapDrawable(toBitmap(bytes));
		}
		return drawable;
	}
	
	public static BitmapDrawable toBitmapDrawable(Drawable drawable) {
		BitmapDrawable bitmapDrawable = null;
		if (drawable != null) {
			bitmapDrawable  = (BitmapDrawable) drawable;
		}
		return bitmapDrawable;
	}
	
	public static Bitmap toBitmap(byte[] bytes) {
		Bitmap bitmap = null;
		if (bytes != null) {
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		return bitmap;
	}
	
	public static Bitmap toBitmap(Drawable drawable) {
		Bitmap bitmap = null;
		if (drawable != null) {
			bitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		return bitmap;
	}
	
	public static Bitmap loadBitmap(int id) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeResource(ZApplication.getInstance().getResources(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap resize(Bitmap bitmap, float newWidth, float newHeight, boolean filter){
		
		Bitmap resizedBitmap = null;
		if(bitmap != null && newWidth > 0 && newHeight > 0){
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float scaleWidth = newWidth / width;
			float scaleHeight = newHeight / height;
			Matrix matrix = new Matrix();
			matrix.setScale(scaleWidth, scaleHeight);
			Log.e(TAG, "createBitmap width:" + width + ", height:" + height + ", scaleWidth:" + scaleWidth + ", scaleHeight:" + scaleHeight);
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, filter);
		}
		return resizedBitmap;
	}
	
	public static Bitmap rotate(Bitmap bitmap, float rotate, boolean filter){
		Bitmap resizedBitmap = null;
		if(bitmap != null){
			Matrix matrix = new Matrix();
			matrix.setRotate(rotate);
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, filter);
		}
		return resizedBitmap;
	}

}
