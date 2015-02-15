package cn.zipper.framwork.canvas;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cn.zipper.framwork.canvas.ModuleInterface;

public class Shell extends SurfaceView implements SurfaceHolder.Callback, Runnable{
	
	private static Shell shell;
	private Activity activity;
	private SurfaceHolder holder;
	private ModuleInterface MI;//ʵ��������ӿڵĶ���;
	private Thread thread;
	private Canvas canvas;
	private Paint paint;
	private boolean running;//�����߳�ѭ��;
	private boolean pause;//�����߳̿���;
	private long threadSleepMillisecond;//�߳��������ʱ��.
	private int screenWidth;
	private int screenHeight;
	private int backgroundColor;
	private int FPS;
	private int screenTipsColor;
	
	
	
	
	private Shell(Activity activity, long threadSleepMillisecond, int backgroundColor) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
        this.setFocusable(true);
        this.setLongClickable(true);
        this.threadSleepMillisecond = Math.abs(threadSleepMillisecond);
        this.backgroundColor = backgroundColor;
        this.running = true;
        holder = this.getHolder();
		holder.addCallback(this);
        paint = new Paint();
        DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
        Log.e("", "*************** Shell.Shell();");
	}
	
	public static Shell createNewInstance(Activity activity, long threadSleepMillisecond, int backgroundColor){
		return shell = new Shell(activity, threadSleepMillisecond, backgroundColor);
	}
	
	public static Shell getInstance(){
		return shell;
	}
	
	public static boolean hasInstance(){
		return shell != null;
	}
	
	public static void cleanInstance(){
		if(shell != null){
			shell.pause();
			shell.setCurrent(null);
			shell.running = false;
			shell = null;
		}
	}
	
	public int getScreenWidth(){
		return screenWidth;
	}
	
	public int getScreenHeight(){
		return screenHeight;
	}
	
	public void pause(){
		pause = true;
	}
	
	public void continueToRun(){
		pause = false;
	}
	
	public void setCurrent(ModuleInterface MI){
		this.MI = MI;
	}
	
	public Activity getActivity(){
		return activity;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		Log.e("", "*************** Shell::surfaceChanged().");
		screenWidth = width;
		screenHeight = height;
		continueToRun();
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.e("", "*************** Shell::surfaceCreated().");
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.e("", "*************** Shell::surfaceDestroyed().");
		Shell.cleanInstance();
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean b = false;
		if(MI != null){
			b = MI.onKey(keyCode, event);
		}
		return b;
	};
	
	
	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		boolean b = false;
		if(MI != null){
			int x = (int)event.getX();
			int y = (int)event.getY();
			b = MI.onTouch(event.getAction(), x, y);
		}
		return b;
	};
	
	
	private void onUpdate(){
		if (MI != null) {
			MI.onUpdate();
		}
	}
	
	
	private void onDraw(){
		canvas = holder.lockCanvas(null);// ��ȡ����
		if (canvas != null) {
			screenWidth = canvas.getWidth();
			screenHeight = canvas.getHeight();
			clearScreen();
			if (MI != null) {
				MI.onDraw(canvas, paint);
			}
			drawScreenTips(canvas, paint);
			holder.unlockCanvasAndPost(canvas);// �������ύ���õ�ͼ��
		}
	}
	
	private void drawScreenTips(Canvas canvas, Paint paint){
		paint.setColor(screenTipsColor);
		paint.setTextSize(15);
		canvas.drawText("FPS: " + FPS, 4, 15, paint);
	}
	
	/**
	 * ����.
	 * �ú�ɫ������Ļ, ��Щ�����л�����ʱ�������ز���, ����������ǿ������.
	 */
	private void clearScreen(){
		paint.setColor(backgroundColor);
		canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
	}
	
	
	public void run() {
		// TODO Auto-generated method stub
		try{
			Log.e("", "*************** Shell::run() ***************");
			
			long startMillisecond = 0;//��ʼ������;
			long realTimeSleepMillisecond = 0;//ʵʱ���ߺ�����;
			long allConsumptionMillisecond = 0;//�������;
			int tempFPS = 0;
			int fpsCount = 0;
			
			while (running) {
				startMillisecond = System.currentTimeMillis();
				if(!pause){
					onUpdate();
					onDraw();
				}
				realTimeSleepMillisecond = threadSleepMillisecond - (System.currentTimeMillis() - startMillisecond);
				if(realTimeSleepMillisecond > 0){
					Thread.sleep(realTimeSleepMillisecond);
				}else{
					screenTipsColor = 0xffff0000;
					Thread.yield();//����;
				}
				allConsumptionMillisecond = System.currentTimeMillis() - startMillisecond;
				if(allConsumptionMillisecond != 0){
					tempFPS += (int)(1000L/allConsumptionMillisecond);
					fpsCount ++;
					if(fpsCount == 5){//��֡����һ��FPSֵ;
						FPS = tempFPS/fpsCount;
						tempFPS = 0;
						fpsCount = 0;
						screenTipsColor = 0xff005FFc;
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			Log.e("Shell.run()", e.toString());
			e.printStackTrace();
		}
	}

	

	

}
