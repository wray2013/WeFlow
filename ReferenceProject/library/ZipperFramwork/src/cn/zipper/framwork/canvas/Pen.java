package cn.zipper.framwork.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public final class Pen {
	
	
//	Bitmap logo = BitmapLoader.getBitmap(R.drawable.oxy_logo);
//	logo = Graphics.rotate(logo, r, true);
//	
//	Shape shape = new OvalShape();
//	
//	ShapeDrawable shapeDrawable = new ShapeDrawable();
//	BitmapShader bitmapShader = new BitmapShader(
//			logo,
//			Shader.TileMode.REPEAT,
//			Shader.TileMode.REPEAT);
//	shapeDrawable.getPaint().setShader(bitmapShader);
//	shapeDrawable.setShape(shape);
//	
//	shapeDrawable.setBounds(
//			44,
//			44,
//			195,
//			195);
//	shapeDrawable.draw(canvas);
	//-------------------------------------------------------------
//	// ���ù�Դ�ķ���    
//	float[] direction = new float[]{ 1, 1, 2 };    
//	//���û������v�    
//	float light = 0.04f;    
//	// ѡ��ҪӦ�õķ���ȼ�    
//	float specular = 8.2f;
//	// ��maskӦ��һ�������ģ��    
//	float blur = 3.5f;    
//	EmbossMaskFilter emboss = new EmbossMaskFilter(direction,light,specular,blur);    
//	
//	BlurMaskFilter blurMaskFilter = new BlurMaskFilter(33, BlurMaskFilter.Blur.INNER);
//	// Ӧ��mask    
//	
//	paint.setMaskFilter(blurMaskFilter);
	
	public static void drawRect(ZRect rect, Canvas canvas, Paint paint){
		canvas.drawRect(rect.getX(), rect.getY(), rect.getRightX(), rect.getBottomY(), paint);
	}
	
	
	/**
	 * ��pointΪ���ĵ�, ������;
	 * @param point
	 * @param rect
	 * @param canvas
	 * @param paint
	 */
	public static void drawRectAtPointByCenter(XPoint point, ZRect rect, Canvas canvas, Paint paint){
		float tempWidth = rect.getWidth()/2;
		float tempHeight = rect.getHeight()/2;
		canvas.drawRect(
				point.getX() - tempWidth, 
				point.getY() - tempHeight, 
				point.getX() + tempWidth, 
				point.getY() + tempHeight, 
				paint);
	}
	
	public static void drawRectAtPointByBottom(XPoint point, ZRect rect, Canvas canvas, Paint paint){
		float tempWidth = rect.getWidth()/2;
		canvas.drawRect(
				point.getX() - tempWidth, 
				point.getY() - rect.getHeight(), 
				point.getX() + tempWidth, 
				point.getY(), 
				paint);
	}
	
	public static void drawBitmapAtPointByBottom(XPoint point, Bitmap bitmap, Canvas canvas, Paint paint){
		int tempWidth = bitmap.getWidth()/2;
		canvas.drawBitmap(bitmap, point.getX() - tempWidth, point.getY() - bitmap.getHeight(), paint);
	}
	
	public static void drawRectAtPoint3DByCenter(XPoint3D point, ZRect rect, Canvas canvas, Paint paint){
		float tempWidth = rect.getWidth()/2;
		float tempHeight = rect.getHeight()/2;
		canvas.drawRect(
				point.getX() - tempWidth, 
				point.getY() - tempHeight - (int)Math.sqrt(point.getZ()*point.getZ()/2), //z�����y�����ӳ��, ������45�ȸ���.
				point.getX() + tempWidth, 
				point.getY() + tempHeight, 
				paint);
	}
	
	
	public static void drawPixels(int x, int y, PixelsBitmap pixelsBitmap, Canvas canvas, Paint paint){
		canvas.drawBitmap(
				pixelsBitmap.pixels(), //colors
				0, //offset
				pixelsBitmap.width(), //stride
				x, //x
				y, //y
				pixelsBitmap.width(), //width
				pixelsBitmap.height(), //height
				true, //hasAlpha
				paint);//paint
	}
	
	
	public static void fillRectByBitmap(ZRect rect, Bitmap bitmap, Canvas canvas, Paint paint){
		if(rect != null && bitmap != null){
			float x = rect.getX();
			float y = rect.getY();
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			canvas.save();
			canvas.clipRect(rect.getRectObject());
			for(int i=0; i<rect.getWidth()/bitmapWidth+1; i++){
				for (int j = 0; j<rect.getHeight()/bitmapHeight+1; j++) {
					canvas.drawBitmap(bitmap, x + bitmapWidth * i, y + bitmapHeight * j, paint);
				}
			}
			canvas.restore();
		}
	}
	
	
	
}
