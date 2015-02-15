package cn.zipper.framwork.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import cxx.oxy.engine.core.ModuleInterface;
import cxx.oxy.engine.core.ModuleManager;

public class Scene implements ModuleInterface{
	
	private static Scene scene;
	
	private ModuleManager manager;
	private Map map;
	private Camera camera;
	
	
	private Scene(){
	}
	
	
	
	public static Scene getInstanse(){
		if(scene == null){
			scene = new Scene();
		}
		return scene;
	}
	
	
	public static void cleanInstanse(){
		scene = null;
	}
	
	
	
	/**
	 * @param bitmap: �������ĵ�ͼͼƬ
	 * @param data: �������ĵ�ͼ���
	 * @param blockSize: ��������ͼ��ͼ���С
	 * @param x: �������Ļ���
	 * @param y: �������Ļ���
	 * @param width: �������Ļ���
	 * @param height: �������Ļ�߶�
	 */
	public void setScene(Bitmap bitmap, int[][] data, int[][] data2, int blockSize, int x, int y, int width, int height){
		map = null;
		map = new Map(this);
		map.setMap(bitmap, data, data2, blockSize);
		
		camera = null;
		camera = new Camera(this, x, y, width, height);
		camera.setMap(map);
		
		manager.removeAll();
		manager = null;
		manager = new ModuleManager();
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public Map getMap(){
		return map;
	}
	
	public ModuleManager getModuleManager(){
		return manager;
	}
	
	
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public void onDraw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		camera.onDraw(canvas, paint);
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
		map.onUpdate();
		camera.onUpdate();
		manager.onUpdate();
	}
	
	

}
