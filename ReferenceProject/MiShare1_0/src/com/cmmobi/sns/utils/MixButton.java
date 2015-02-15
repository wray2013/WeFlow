package com.cmmobi.sns.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义Textview 在Textview上添加图片
 * 
 * @author Administrator
 * 
 */
public class MixButton extends TextView {

	private final String namespace = "http://net.looklook.mobile";

	private int resourceid = 0;// 资源id

	private Bitmap bitmap;

	public MixButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MixButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MixButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		resourceid = attrs.getAttributeResourceValue(namespace, "icon", 0);

		bitmap = BitmapFactory.decodeResource(getResources(), resourceid);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (bitmap != null) {
			Rect src = new Rect();
			// 将截取的图像复制到bitmap上的目标区域，在本例中与复制区域相同
			Rect target = new Rect();
			src.left = 0;
			src.top = 0;
			src.right = bitmap.getWidth();
			src.bottom = bitmap.getHeight();
			int textHeight = (int) getTextSize();
			target.left = 0;
			// 计算图像复制到目标区域的纵坐标。由于TextView组件的文本内容并不是
			// 从最顶端开始绘制的，因此，需要重新计算绘制图像的纵坐标
			target.top = (int) ((getMeasuredHeight() - getTextSize()) / 2) + 1;
			target.bottom = target.top + textHeight;
			// 为了保证图像不变形，需要根据图像高度重新计算图像的宽度
			target.right = (int) (textHeight * (bitmap.getWidth() / (float) bitmap
					.getHeight()));
			// 开始绘制图像
			canvas.drawBitmap(bitmap, src, target, getPaint());
			// 将TextView中的文本向右移动一定的距离（在本例中移动了图像宽度加2个象素点的位置）
			canvas.translate(target.right + 2, 0);
		}
		super.onDraw(canvas);
	}

}
