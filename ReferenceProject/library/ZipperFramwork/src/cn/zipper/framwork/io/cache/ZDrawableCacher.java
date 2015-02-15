package cn.zipper.framwork.io.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import cn.zipper.framwork.utils.MD5Util;
import cn.zipper.framwork.utils.ZGraphics;

public final class ZDrawableCacher extends ZBaseCacher<Drawable> {
	
	private CompressFormat format;
	private int quality;
	
	public ZDrawableCacher(File path, int poolSize, CompressFormat format, int quality) {
		super(path, poolSize);
		this.format = format;
		this.quality = quality;
	}
	
	public ZDrawableCacher(String path, int poolSize, CompressFormat format, int quality) {
		super(path, poolSize);
		this.format = format;
		this.quality = quality;
	}
	

	@Override
	protected Drawable readFile(String name) {
//		File uri = new File(file, name);
//		Bitmap bitmap = BitmapFactory.decodeFile(uri.getAbsolutePath());
//		return ZGraphics.toBitmapDrawable(bitmap);
		
		return null;
	}

	@Override
	protected boolean saveFile(String name, Drawable drawable) {
		boolean b = false;
//		FileOutputStream fileOutputStream = null;
//		try {
//			BitmapDrawable bitmapDrawable = ZGraphics.toBitmapDrawable(drawable);
//			
//			File uri = new File(file, name);
//			fileOutputStream = new FileOutputStream(uri);
//			bitmapDrawable.getBitmap().compress(format, quality, fileOutputStream);
//			fileOutputStream.flush();
//			b = true;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			b = false;
//			
//		} finally {
//			try {
//				if (fileOutputStream != null) {
//					fileOutputStream.close();
//					fileOutputStream = null;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		return b;
	}

	@Override
	protected Drawable load(String name) {

		BitmapDrawable drawable = null;
		try {
			drawable = new BitmapDrawable(new URL(name).openStream());
		} catch (Exception e) {
			e.printStackTrace();
			drawable = null;
		}
		
		return drawable;
	}
	
	@Override
	protected String formatName(String name) {
		return MD5Util.getMD5String(name);
	}

}
