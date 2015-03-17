package com.cmmobi.railwifi.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-6-18
 */
public class BitmapUtils {

	private static final String TAG = "BitmapUtils";

	public static Bitmap getBigPicture(Context context, Uri uri,int width) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(uri, null, null, null, null);
		cursor.moveToFirst();
		String path = "";
		path=cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
		String orientation = cursor.getString(cursor.getColumnIndex("orientation"));// 获取旋转的角度
		cursor.close();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = options.outWidth / width;
		int height = options.outHeight/options.inSampleSize;
		options.outWidth = width;
		options.outHeight = height;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		if(DateUtils.isNum(orientation)){
			Log.d(TAG, "orientation="+orientation);
			int angle=Integer.parseInt(orientation);
			 Matrix m = new Matrix();  
             width = bmp.getWidth();  
             height = bmp.getHeight();  
             m.setRotate(angle); // 旋转angle度  
             bmp = Bitmap.createBitmap(bmp, 0, 0, width, height,  
                     m, true);// 从新生成图片  
		}
		return bmp;
	}

	/**
	 * 获取圆形头像
	 */
	public static Bitmap getPortraitBitmap(Bitmap portrait) {
		int width = portrait.getWidth();
		int height = portrait.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;

		//计算圆形区域
		if (width <= height) {
			roundPx = width / 2;
			float clip = (height - width) / 2;
			top = clip;
			bottom = width + clip;
			left = 0;
			right = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;

			left = clip;
			right = height + clip;
			top = 0;
			bottom = height;

			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		paint.setColor(0xFFFFFFFF);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(portrait, src, dst, paint);
		return output;
	}
	
	public static Bitmap cropBitmap(String inPath, int width,int xoffset,int yoffset,int outWidth) {
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			fs = new FileInputStream(inPath);
			bs = new BufferedInputStream(fs);
			BitmapRegionDecoder mDecoder = BitmapRegionDecoder.newInstance(bs, true);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opt.inDither = false;
			
			float scale = width / outWidth;
			if (scale > 1) {
				opt.inSampleSize  = width / outWidth + 1;
			} else {
				opt.inSampleSize = 1;
			}
			Rect rect = new Rect(xoffset, yoffset, xoffset + width, yoffset + width);
			Log.d(TAG,"rect = " + rect + " option = " + opt.inSampleSize);
			return mDecoder.decodeRegion(rect, opt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bs.close();
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static Bitmap readBitmapAutoSize(String filePath, int outWidth,
			int outHeight) {
		// outWidth和outHeight是目标图片的最大宽度和高度，用作限制
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			fs = new FileInputStream(filePath);
			bs = new BufferedInputStream(fs);
			BitmapFactory.Options options = setBitmapOption(filePath, outWidth,
					outHeight);
			return BitmapFactory.decodeStream(bs, null, options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bs.close();
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static BitmapFactory.Options setBitmapOption(String file,
			int width, int height) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
		BitmapFactory.decodeFile(file, opt);

		int outWidth = opt.outWidth; // 获得图片的实际高和宽
		int outHeight = opt.outHeight;
		
		opt.inDither = false;
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
		opt.inSampleSize = 1;
		// 设置缩放比,1表示原比例，2表示原来的四分之一....
		// 计算缩放比
		int shortLen = outWidth < outHeight ? outWidth : outHeight;
		int shortEdge = width < height ? width : height;
		if (shortLen != 0 && shortEdge != 0) {
			if (shortLen % shortEdge < 10) {
				opt.inSampleSize = shortLen / shortEdge;
			} else {
				opt.inSampleSize = shortLen / shortEdge + 1;
			}
			if (opt.inSampleSize < 1) {
				opt.inSampleSize = 1;
			}
		}
		Log.d("==WJM==","width = " + width + " height = " + height + " outWidth = " + outWidth + " outHeight = " + outHeight + " opt.inSampleSize = " + opt.inSampleSize);
		opt.inJustDecodeBounds = false;// 最后把标志复原
		return opt;
	}
	
	public static BitmapFactory.Options getBitmapOption(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
		BitmapFactory.decodeFile(filePath, opt);
		return opt;
	}
	
	public static boolean saveBitmap2file(Bitmap bmp, String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 50;
		File dstFile = new File(filename);
        if(!dstFile.getParentFile().exists()) {
        	dstFile.getParentFile().mkdirs();
		}
        
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return bmp.compress(format, quality, stream);
	}
	
	public static boolean saveBitmap2file(Bitmap bmp, int quality,String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		File dstFile = new File(filename);
        if(!dstFile.getParentFile().exists()) {
        	dstFile.getParentFile().mkdirs();
		}
        
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return bmp.compress(format, quality, stream);
	}
	
	//bitmap转byte数组
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();// 将Bitmap压缩成jpeg编码，质量为100%存储
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);// 除了PNG还有很多常见格式，如jpeg等。
		return os.toByteArray();
	}
	
	
	
	public static Bitmap createBitmap(Bitmap source, int x, int y, int width,
            int height, Matrix m, boolean filter) {
		if (x + width > source.getWidth()) {
			throw new IllegalArgumentException("x + width must be <= bitmap.width()");
		}
		if (y + height > source.getHeight()) {
			throw new IllegalArgumentException("y + height must be <= bitmap.height()");
		}
		
		// check if we can just return our argument unchanged
		if (!source.isMutable() && x == 0 && y == 0
			&& width == source.getWidth() && height == source.getHeight()
			&& (m == null || m.isIdentity())) {
			return source;
		}
		
		if (m == null || m.isIdentity()) {
			return source;
		}
		
		int neww = width;
        int newh = height;
        Paint paint;

        Rect srcR = new Rect(x, y, x + width, y + height);
        RectF dstR = new RectF(0, 0, width, height);

        /*  the dst should have alpha if the src does, or if our matrix
            doesn't preserve rectness
        */
        boolean hasAlpha = source.hasAlpha() || !m.rectStaysRect();
        RectF deviceR = new RectF();
        m.mapRect(deviceR, dstR);
        neww = Math.round(deviceR.width());
        newh = Math.round(deviceR.height());
		
		
		Bitmap newBitmap = Bitmap.createBitmap(neww, newh, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		
		canvas.translate(-deviceR.left, -deviceR.top);
		canvas.concat(m);
		paint = new Paint();
		paint.setFilterBitmap(filter);
		if (!m.rectStaysRect()) {
			paint.setAntiAlias(true);
		}
		
		canvas.drawBitmap(source, srcR, dstR, paint);
		
		return newBitmap;
		}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {  
        // 取 drawable 的长宽  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
  
        // 取 drawable 的颜色格式  
        Bitmap.Config config = Bitmap.Config.ARGB_8888;  
        // 建立对应 bitmap  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        // 建立对应 bitmap 的画布  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        // 把 drawable 内容画到画布中  
        drawable.draw(canvas);  
        return bitmap;  
    }  
	
	public static Bitmap rotate(Bitmap bitmap, float rotate, boolean filter){
		Bitmap resizedBitmap = null;
		if(bitmap != null){
			Matrix matrix = new Matrix();
			matrix.setRotate(rotate);
			resizedBitmap = BitmapUtils.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, filter);
		}
		return resizedBitmap;
	}
	
	public static void releaseBmp(Bitmap bmp) {
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
	}
	
	public static void releaseBmps(Bitmap[] bmps) {
		if (bmps != null) {
			for (Bitmap bmp:bmps) {
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
					bmp = null;
				}
			}
			bmps = null;
		}
	}
}
