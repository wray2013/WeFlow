package com.cmmobi.looklook.common.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;



public class TextDrawable extends BitmapDrawable {

    private final String text;
    private final Paint paint;    
    private final Resources res;
    private final int backgrandID;

    public TextDrawable(Resources res, int RID, String text) {

    	//super(BitmapFactory.decodeResource(res, RID).copy(Bitmap.Config.ARGB_8888, true));
        super();
    	this.text = text;
        this.res = res;
        backgrandID = RID;

        this.paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(22f);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setShadowLayer(6f, 0, 0, Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }


    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		Bitmap bm = BitmapFactory.decodeResource(res, backgrandID).copy(Bitmap.Config.ARGB_8888, true);
		canvas.setBitmap(bm);

		canvas.drawText(text, 0, 0, paint);
		super.draw(canvas);
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		paint.setColorFilter(cf);
		
	}
}