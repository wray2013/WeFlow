package com.cmmobi.railwifi.utils;

import java.io.IOException;

import com.cmmobi.railwifi.MainApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Log;

public final class ZGraphics {
	
	private static final String TAG = "ZGraphics";

	private ZGraphics() {
	}
	
	public static void drawNinepath(Canvas canvas, int id, Rect rect) {
		Bitmap bitmap = BitmapFactory.decodeResource(MainApplication.getAppInstance().getResources(), id);
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
			bitmap = BitmapFactory.decodeResource(MainApplication.getAppInstance().getResources(), id);
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
	
	public static int getExifOrientation(String filepath) {
		Log.i(TAG, "[getExifOrientation] filepath:"+filepath);
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
        	Log.e(TAG, "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, -1);
            Log.i(TAG, "[getExifOrientation] orientation:"+orientation);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        Log.i(TAG, "[getExifOrientation] degree:"+degree);
        return degree;
    }
	
	public static int setExifOrientation(String filepath,int orientation) {
		Log.i(TAG, "[setExifOrientation] filepath:"+filepath);
        int degree = 0;
        if (orientation != -1) {
            // We only recognize a subset of orientation tag values.
            switch(orientation) {
                case 90:
                    degree = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                case 180:
                    degree = ExifInterface.ORIENTATION_ROTATE_180;
                    break;
                case 270:
                    degree = ExifInterface.ORIENTATION_ROTATE_270;
                    break;
                 default:
                	degree = 0;
                	break;
            }

        }
        
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
        	Log.e(TAG, "cannot read exif", ex);
        }
        if (exif != null) {
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, degree+"");
            Log.i(TAG, "[getExifOrientation] orientation:"+orientation);
            try {
				exif.saveAttributes();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
        }
        Log.i(TAG, "[getExifOrientation] degree:"+degree);
        return degree;
    }

}
