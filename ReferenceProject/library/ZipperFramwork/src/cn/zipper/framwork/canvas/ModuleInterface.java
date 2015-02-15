package cn.zipper.framwork.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;

public interface ModuleInterface {
	
	public abstract void init();
	public abstract boolean onKey(int keyCode, KeyEvent keyEvent);
	public abstract boolean onTouch(int event, int x, int y);
	public abstract void onUpdate();
	public abstract void onDraw(Canvas canvas, Paint paint);
	
}
