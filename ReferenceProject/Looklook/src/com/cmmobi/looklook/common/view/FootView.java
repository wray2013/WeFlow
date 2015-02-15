package com.cmmobi.looklook.common.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.cmmobi.looklook.R;

public class FootView extends View {

	Bitmap bitmap;

	Paint paint = new Paint();

	List<Point> points = new ArrayList<Point>();

	public FootView(Context context) {
		super(context);
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.temp_jiaoyin);
	}

	public FootView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.temp_jiaoyin);
	}

	public FootView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.temp_jiaoyin);
	}

	public void addPoint(Point point) {
		points.add(point);
	}

	public void addPointList(ArrayList<Point> points) {
		this.points.clear();
		this.points.addAll(points);
	}
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (int i = 0; i < points.size(); i++) {

			canvas.drawBitmap(bitmap, points.get(i).x, points.get(i).y, paint);

		}
	}
}