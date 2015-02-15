package cn.zipper.framwork.canvas;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.view.KeyEvent;
import cxx.oxy.engine.R;
import cxx.oxy.engine.core.ModuleInterface;
import cxx.oxy.engine.core.Shell;
import cxx.oxy.engine.graphics.BitmapLoader;
import cxx.oxy.engine.graphics.Graphics;
import cxx.oxy.engine.graphics.Pen;
import cxx.oxy.engine.graphics.XRect;
import cxx.oxy.engine.utils.trend.TrendGroup;
import cxx.oxy.engine.utils.trend.TrendGroupListener;

public class GameLogic implements ModuleInterface, TrendGroupListener{
	
	private static GameLogic gameLogic;
	
	private GameLogic(){
		init();
	}
	
	public static boolean hasInstance(){
		return gameLogic != null;
	}
	
	public static GameLogic createNewInstance(){
		cleanInstance();
		return gameLogic = new GameLogic();
	}
	
	public static GameLogic getInstance(){
		return gameLogic;
	}
	
	public static void cleanInstance(){
		gameLogic = null;
	}
	
	//----------------------------------------------------------------------------------------------
	
	
	int[][] data2 = {
			{2, 2, 2, 2, 2, 2, 2, 2, 6, 6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{2, 2, 6, 2, 6, 2, 6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{2, 2, 2, 6, 2, 6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{2, 2, 2, 2, 6, 6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2},
			{2, 2, 4, 2, 4, 4, 4, 4, 4, 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2},
			{2, 2, 4, 2, 4, 2, 2, 2, 4, 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2},
			{2, 2, 4, 2, 4, 2, 4, 2, 4, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 3},
			{2, 2, 4, 2, 4, 4, 4, 2, 4, 2, 2, 2, 2, 1, 1, 2, 2, 2, 3, 3},
			{2, 2, 4, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2},
			{2, 2, 4, 4, 4, 4, 4, 4, 4, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2, 2, 2, 2},
			{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2}
	};
	
	int[][] data3 = {
			{0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 7, 0, 7, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	};
	
	TrendGroup trendGroup;
	TrendGroup trendGroupX;
	TrendGroup trendGroupY;
	TrendGroup trendGroupRotate;
	TrendGroup trendGroupSize;

	public void init() {
		// TODO Auto-generated method stub
		trendGroup = new TrendGroup(TrendGroup.MODE_AVERAGE, -1, this);
		trendGroup.appendNode(55, 100);
		trendGroup.appendNode(255, 100);
		trendGroup.setLastNode(55);
		
		trendGroupX = new TrendGroup(TrendGroup.MODE_AVERAGE, -1, this);
		trendGroupX.appendNode(0, 10);
		trendGroupX.appendNode(140, 5);
		trendGroupX.setLastNode(110);
		
		trendGroupY = new TrendGroup(TrendGroup.MODE_APPROACH, -1, this);
		trendGroupY.appendNode(5, 20);
		trendGroupY.appendNode(50, 40);
//		trendGroupY.appendNode(70, 99);
		trendGroupY.setLastNode(5);
		
		trendGroupRotate = new TrendGroup(TrendGroup.MODE_AVERAGE, -1, this);
		trendGroupRotate.appendNode(0, 30);
		trendGroupRotate.setLastNode(360);
		
		trendGroupSize = new TrendGroup(TrendGroup.MODE_AVERAGE, -1, this);
		trendGroupSize.appendNode(10, 30);
		trendGroupSize.setLastNode(100);
	}
	
	
	Bitmap bitmap = BitmapLoader.getBitmap(R.drawable.oxy_logo);
	
	int alpha = 0;
	
	float r = 3.004F;
	
	public void onDraw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		
		
//		paint.setColor(0xff0000ff);
//		XRect rect = new XRect(0, 0, Shell.getInstance().getScreenWidth(), Shell.getInstance().getScreenHeight());
//		Pen.drawRect(rect, canvas, paint);
		
		
		int a = (int)trendGroup.runToNextValue();
		System.out.println("Alpha = " + a);
		paint.setAlpha(a);
		
		
		
		int tempWidth = (int) (trendGroupSize.runToNextValue() * bitmap.getWidth() / 100);
		int tempHeight = (int) (trendGroupSize.runToNextValue() * bitmap.getHeight() / 100);
		Bitmap bitmap2 = Graphics.resize(bitmap, tempWidth, tempHeight, true);
		Bitmap bitmap3 = Graphics.rotate(bitmap2, trendGroupRotate.runToNextValue(), true);
		
		float x = trendGroupX.runToNextValue();
		float y = trendGroupY.runToNextValue();
		
		for(int i=10; i<50;i++){
			canvas.drawBitmap(bitmap3, x,y + (i*3), null);
		}
		
//		trendGroup.stop();
		
		Bitmap bitmap1 = Graphics.rotate(bitmap, 10, true);
		canvas.drawBitmap(bitmap1, 0, 0, paint);
		
	}
	

	public boolean onKey(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		for(int i=0; i<9999; i++){
			float aaa = 10/0.121312343238f;
		}
		return false;
	}

	public boolean onTouch(int event, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onUpdate() {
		// TODO Auto-generated method stub
		
	}

	public void onTrendGroupLoop(TrendGroup trendGroup) {
		// TODO Auto-generated method stub
		if(trendGroup == trendGroupRotate || trendGroup == trendGroupSize){
			trendGroupRotate.reset(false);
			trendGroupSize.reset(false);
		}
	}

	public void onTrendGroupStop(TrendGroup trendGroup) {
		// TODO Auto-generated method stub
	}
	
	
	
	
	

}
