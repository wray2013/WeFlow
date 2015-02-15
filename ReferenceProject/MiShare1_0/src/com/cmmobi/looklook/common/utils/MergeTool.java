package com.cmmobi.looklook.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;

import com.cmmobi.looklook.R;


public class MergeTool
{
	private final String TAG = MergeTool.class.getSimpleName();
	
	private Context mContext = null;
	
	public enum ENUM_TYPE
	{
		TYPE_URL, // 图片类型
//		TYPT_PATH,
		TYPE_ID, // 资源id
	}
	
	// 图片的长宽
	private final int PIC_WIDTH = 200;
	private final int PIC_HEIGHT = 200;
	
	public static class DiaryImage
	{
		public ENUM_TYPE type;
		public String text;
		public String url;
		public int id;
	}
	
	public MergeTool(Context context)
	{
		mContext = context;
	}
	
	private List<Bitmap> mListBitmap = null;
	
	// url 测试用
	public Bitmap getBitmapSync(String ...src)
	{
		if(src != null && src.length > 0)
		{
			mListBitmap = new ArrayList<Bitmap>();
			for(String url : src)
			{
				if(isFileExists(url))
				{
					Bitmap bitmap = modifyImage(BitmapFactory.decodeFile(url));
					mListBitmap.add(bitmap);
				}
				else
				{
					Bitmap bitmap = modifyImage(loadBitmap(url));
					if(bitmap != null)
					{
						mListBitmap.add(bitmap);
					}
				}
			}
		}
		
		return merge();
	}
	
	// 同步
	public Bitmap getBitmapSync(ArrayList<DiaryImage> images)
	{
		if(images != null && images.size() > 0)
		{
			mListBitmap = new ArrayList<Bitmap>();
			for(DiaryImage image : images)
			{
				switch(image.type)
				{
				case TYPE_URL:
					if(isFileExists(image.url))
					{
						// 直接加载
						Bitmap bitmap = modifyImage(BitmapFactory.decodeFile(image.url));
						if(image.id != 0)
						{
							bitmap = coverBitmap(bitmap, image.id);
						}
						mListBitmap.add(bitmap);
					}
					else
					{
						// 网络加载
						Bitmap bitmap = modifyImage(loadBitmap(image.url));
						if(image.id != 0)
						{
							bitmap = coverBitmap(bitmap, image.id);
						}
//						if(bitmap != null)
						{
							mListBitmap.add(bitmap);
						}
					}
					break;
				case TYPE_ID:
					// 资源加载
					Bitmap bitmap = getBitmapByResource(image);
					if(bitmap != null)
					{
						mListBitmap.add(bitmap);
					}
					break;
				}
			}
		}
		
		return merge();
	}
	
	// 根据 mListBitmap 合并图片
	private Bitmap merge()
	{
		if(mListBitmap.size() > 0)
		{
			Bitmap bitmap = Bitmap.createBitmap(PIC_WIDTH, PIC_HEIGHT * mListBitmap.size() + mListBitmap.size() - 1, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			
			for(int i = 0; i < mListBitmap.size(); i ++)
			{
				Bitmap tmp = mListBitmap.get(i);
				if(tmp == null)
				{
					tmp = getDefaultBitmap();
				}
				Rect src = new Rect(0, 0, tmp.getWidth(), PIC_HEIGHT);
				Rect dst = new Rect(0, (PIC_HEIGHT+1)*i, tmp.getWidth(), (PIC_HEIGHT+1)*i+PIC_HEIGHT);
				canvas.drawBitmap(tmp, src, dst, paint);
			}
			canvas.save(Canvas.ALL_SAVE_FLAG);
//			canvas.restore();
			return bitmap;
		}
		
		else
		{
			return null;
		}
		
	}
	
	// 网络加载
	private Bitmap loadBitmap(String url)
	{
		Bitmap bitmap = null;
		try {
			Log.v(TAG, "download url = " + url);

//			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//			connection.connect();
//			InputStream is = connection.getInputStream();
//			
//			int readLength = 0;
//			byte[] temp = new byte[4096];
//			ByteArrayBuffer buffer = new ByteArrayBuffer(4096);
//			while (true) {
//				readLength = is.read(temp);
//				if (readLength == -1) {
//					break;
//				} else {
//					buffer.append(temp, 0, readLength);
//				}
//			}
//			byte[] imageData = buffer.toByteArray();
			
			ZHttp2 http2 = new ZHttp2();
			ZHttpResponse httpResponse = http2.get(url);

			ZHttpReader reader = new ZHttpReader(httpResponse.getInputStream(), null);
			byte[] imageData = reader.readAll(0);
			
			Log.d(TAG, "imageData.length="+imageData.length);
			
			if(imageData.length>0){
				bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return bitmap;
	}
	
	// 对bigmap大小处理
	private Bitmap modifyImage(Bitmap bitmap)
	{
		if(bitmap == null)
		{
			bitmap = getDefaultBitmap();
		}
//		Bitmap newBitmap = null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Log.v(TAG, "bitmap width = " + width);
		Log.v(TAG, "bitmap height = " + height);
		float scaleWidth = ((float)PIC_WIDTH) / ((float)width);
		float scaleHeight = ((float)PIC_HEIGHT) / ((float)height);
		float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
		
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 
//		bitmap.recycle();
//		System.gc();
		return bitmap;
	}
	
	// 叠加图片
	private Bitmap coverBitmap(Bitmap bitmap, int id)
	{
		if(bitmap == null)
		{
			bitmap = getDefaultBitmap().copy(Bitmap.Config.ARGB_8888, true);
		}
		BitmapDrawable drawable = (BitmapDrawable) mContext.getResources().getDrawable(id);
		Bitmap cover = drawable.getBitmap();
		
		// loadBitmap的bitmap.isMutable()为false；
		Bitmap ret = Bitmap.createBitmap(PIC_WIDTH, PIC_HEIGHT, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(ret);
		Paint paint = new Paint();
		
		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Rect src1 = new Rect(0, 0, cover.getWidth(), cover.getHeight());
		Rect dst = new Rect(0, 0, ret.getWidth(), ret.getHeight());
		canvas.drawBitmap(bitmap, src, dst, paint);
		canvas.drawBitmap(cover, src1, dst, paint);
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
//		canvas.restore();
		
		return ret;
	}
	
	// 文字和图片绘制
	private Bitmap getBitmapByResource(DiaryImage image)
	{
		Bitmap bitmap = Bitmap.createBitmap(PIC_WIDTH, PIC_HEIGHT, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		
		BitmapDrawable drawable = (BitmapDrawable) mContext.getResources().getDrawable(image.id);
		Bitmap background = drawable.getBitmap();
		
		Rect src = new Rect(0, 0, background.getWidth(), background.getHeight());
		Rect dst = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		canvas.drawBitmap(background, src, dst, paint);
		
		TextPaint textPaint = new TextPaint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(26f);
		
		if(image.text != null)
		{
			String newStr = "";
			for (int i = 0; i < image.text.length(); i++)
			{
				newStr = image.text.substring(0, i + 1);
				float[] widths = new float[newStr.length()];
				int length = 0;
				textPaint.getTextWidths(newStr, widths);
				for(int j = 0; j < newStr.length(); j++)
				{
					length += widths[i];
				}
				if(length > (PIC_WIDTH - 12) * 2 - 40)
				{
					newStr += "…";
					break;
				}
				
			}
			
			StaticLayout layout = new StaticLayout(newStr, textPaint, PIC_WIDTH - 12, Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
	//		StaticLayout layout = new StaticLayout(aboutTheGame, 0, aboutTheGame.length(), textPaint, PIC_WIDTH, Alignment.ALIGN_NORMAL,
	//				1f, 0f, true, TextUtils.TruncateAt.valueOf("END"), 14);
			canvas.translate(6, PIC_WIDTH / 2 - textPaint.getTextSize() - 2);
			layout.draw(canvas);
		}
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
//		canvas.restore();
		
		return bitmap;
	}
	
	// 文件是否存在
	private boolean isFileExists(String path)
	{
		File f = new File(path);
		if(f.exists() && f.isFile() && f.canRead())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private Bitmap getDefaultBitmap()
	{
//		BitmapDrawable drawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bg_default);
//		
//		return modifyImage(drawable.getBitmap());
		
		return modifyImage(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_default).copy(Bitmap.Config.ARGB_8888, true));
	}

}
