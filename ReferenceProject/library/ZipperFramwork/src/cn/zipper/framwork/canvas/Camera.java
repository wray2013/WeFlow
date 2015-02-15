package cn.zipper.framwork.canvas;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.view.KeyEvent;
import cxx.oxy.engine.core.ModuleInterface;
import cxx.oxy.engine.game.physics.PhysicsObject;
import cxx.oxy.engine.graphics.XPoint;
import cxx.oxy.engine.graphics.XRect;

public class Camera implements ModuleInterface {
	
	
	private Scene scene;
	private ZRect screenRect;//�������Ļ�ľ���(�������������ֻ���Ļ)
	private ZRect observerRect;//�۲��߾���(�����������ڵ�ͼ)
	private ZRect blockRect;
	private Map map;
	private Bitmap screen;//�������Ļ(����)
	private int blockWidth;//������Ҫ��ʾ�Ŀ���
	private int blockHeight;//������Ҫ��ʾ�Ŀ���
	private Canvas pen;
	private XPoint focusPoint;//�Խ���;
	private PhysicsObject focusObject;
	private boolean needCheckMove;//�Ƿ���Ҫ��������Ľ����Ƿ�Խ���ָ��������;
	
	
	public Camera(Scene scene, int x, int y, int width, int height){
		this.scene = scene;
		screenRect = new ZRect(x, y, width, height);
		observerRect = new ZRect();
		observerRect.setWidth(width).setHeight(height);
		focusPoint = new XPoint();
		screen = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		pen = new Canvas(screen);
	}
	
	
	public void setMap(Map map){
		this.map = map;
		blockRect = new ZRect();
		if(this.map != null){
			int size = this.map.getBlockSize();
			blockRect.setWidth(size).setHeight(size);
			blockWidth = (int)(screenRect.getWidth()/size) + 2;
			blockHeight = (int)(screenRect.getHeight()/size) + 2;
		}
	}
	
	
	public void setFocusPoint(int x, int y){
		focusPoint.setX(x).setY(y);
		focusObject = null;
		needCheckMove = true;
	}
	
	
	public XPoint getFocusPoint(){
		return focusPoint;
	}
	
	public void setFocusObject(PhysicsObject physicsObject){
		focusObject = physicsObject;
		needCheckMove = true;
	}
	
	public PhysicsObject getFocusObject(){
		return focusObject;
	}
	
	
	public ZRect getScreenRect(){
		return screenRect;
	}
	
	
	public ZRect getObserverRect(){
		return observerRect;
	}
	
	
	/**
	 * �ƶ�������ڵ�ͼ�е�λ��;
	 * @param offsetX
	 * @param offsetY
	 */
	public void moveCamera(float offsetX, float offsetY){
		observerRect.setX(observerRect.getX() + offsetX).setY(observerRect.getY() + offsetY);
	}
	
	
	/**
	 * ָ��������ڵ�ͼ�е�λ��;
	 * @param x
	 * @param y
	 */
	public void moveCameraTo(int x, int y){
		observerRect.setX(x).setY(y);
	}
	
	/**
	 * �ƶ��������Ļ���ֻ���Ļ�ϵ�λ��;
	 * @param offsetX
	 * @param offsetY
	 */
	public void moveCameraScreen(int offsetX, int offsetY){
		screenRect.setX(screenRect.getX() + offsetX).setY(screenRect.getY() + offsetY);
	}
	
	/**
	 * ָ���������Ļ���ֻ���Ļ�ϵ�λ��;
	 * @param x
	 * @param y
	 */
	public void moveCameraScreenTo(int x, int y){
		screenRect.setX(x).setY(y);
	}
	

	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	int add=0;
	public void onDraw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		paint.setColor(0xffff00ff);//���Դ���
		canvas.drawRect(
				screenRect.getX() - 2, 
				screenRect.getY() - 2, 
				screenRect.getRightX() + 2, 
				screenRect.getBottomY() + 2, 
				paint);
		
		paint.setColor(0xff999999);//�������Ļ����
		pen.drawRect(0, 0, screenRect.getRightX(), screenRect.getBottomY(), paint);
		
		float blockSize = map.getBlockSize();
		int startBlockX = (int)(observerRect.getX()/blockSize);//��ʼ��
		int startBlockY = (int)(observerRect.getY()/blockSize);//��ʼ��
		
//		System.out.println(" observerRect.x = " +  observerRect.getX());
//		System.out.println(" observerRect.y = " +  observerRect.getY());
		
		for(int i=0; i<blockHeight; i++){
			for(int j=0; j<blockWidth; j++){
				int blockValue = map.getValue(startBlockX + j, startBlockY + i);
				if(blockValue != 0){
					blockRect.setX(j*blockSize - observerRect.getX()%blockSize);
					blockRect.setY(i*blockSize - observerRect.getY()%blockSize);
					pen.save();
					pen.clipRect(blockRect.getRectObject());
					pen.drawBitmap(
							map.getBitmap(), 
							blockRect.getX() - blockValue * blockSize, 
							blockRect.getY(), 
							null);
					pen.restore();
				}
			}
			
			ZRect allowRect = new ZRect();
			int tempX = startBlockX;
			int tempY = startBlockY;
			if(tempX < 0){
				tempX = 0;
			}
			if(tempY < 0){
				tempY = 0;
			}
			allowRect.setX(tempX * blockSize);
			allowRect.setY((tempY + i) * blockSize);
			allowRect.setWidth((blockWidth+1) * blockSize);
			allowRect.setHeight(blockSize);
			
			
			Vector<ModuleInterface> objects = Scene.getInstanse().getModuleManager().getPool();
			if(objects != null){
				for(int index=0; index<objects.size(); index++){
					PhysicsObject physicsObject = (PhysicsObject)objects.elementAt(index);
					if(physicsObject.point().getX() >= allowRect.getX() 
							&& physicsObject.point().getX() <= allowRect.getRightX()
							&& physicsObject.point().getY() >= allowRect.getY()
							&& physicsObject.point().getY() <= allowRect.getBottomY()){
						physicsObject.onDraw(pen, paint);
					}
					
				}
			}
			
//			allowRect.setX(allowRect.getX() - observerRect.getX());
//			allowRect.setY(allowRect.getY() - observerRect.getY());
//			add++;
//			if(add%2 == 0){
//				paint.setColor(0xaaff0000);
//			}else{
//				paint.setColor(0xaa0000ff);
//			}
//			pen.drawRect(allowRect.getRectObject(), paint);
			
			
			for(int j=0; j<blockWidth; j++){
				int blockValue2 = map.getValue2(startBlockX + j, startBlockY + i);
				if(blockValue2 != 0){
					blockRect.setX(j*blockSize - observerRect.getX()%blockSize);
					blockRect.setY(i*blockSize - observerRect.getY()%blockSize);
					pen.save();
					pen.clipRect(blockRect.getRectObject());
					pen.drawBitmap(
							map.getBitmap(), 
							blockRect.getX() - blockValue2 * blockSize, 
							blockRect.getY(), 
							null);
					pen.restore();
				}
			}
		}
		
		
//		Bitmap resize = Graphics.resize(screen, screen.getWidth()/2, screen.getHeight()/2);
		canvas.drawBitmap(screen, screenRect.getX(), screenRect.getY(), paint);
		
		
	}

	public boolean onKey(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onTouch(int event, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onUpdate() {
		// TODO Auto-generated method stub
		
		if(focusObject != null){
			focusPoint.setX(focusObject.point().getX());
			focusPoint.setY(focusObject.point().getY());
		}
		
		if(needCheckMove){//������Ҫ�Ż�, ����һֱfind
			if(observerRect.getCenterX() == focusPoint.getX() && observerRect.getCenterY() == focusPoint.getY()){
				needCheckMove = false;
			}else{
				float tempSpeedX = (focusPoint.getX() - observerRect.getCenterX())/10;
				if(tempSpeedX > map.getBlockSize() - 2){
					tempSpeedX = map.getBlockSize() - 2;
				}else if(tempSpeedX < -(map.getBlockSize() - 2)){
					tempSpeedX = -(map.getBlockSize() - 2);
				}
				float tempSpeedY = (focusPoint.getY() - observerRect.getCenterY())/10;
				if(tempSpeedY > map.getBlockSize() - 2){
					tempSpeedY = map.getBlockSize() - 2;
				}else if(tempSpeedY < -(map.getBlockSize() - 2)){
					tempSpeedY = -(map.getBlockSize() - 2);
				}
				moveCamera(tempSpeedX, tempSpeedY);
			}
		}
	}
	
	
	

}
