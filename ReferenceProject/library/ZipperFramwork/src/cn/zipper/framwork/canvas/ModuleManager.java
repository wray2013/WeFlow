package cn.zipper.framwork.canvas;

import java.util.Vector;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;


public final class ModuleManager implements ModuleInterface{
	
	private Vector<ModuleInterface> pool;
	private int startIndex;
	private int endIndex;
	
	public ModuleManager(){
		pool = new Vector<ModuleInterface>();
	}
	
	
	public void append(ModuleInterface moduleInterface){
		if(!pool.contains(moduleInterface)){
			pool.add(moduleInterface);
		}else{
			Log.e("", "ERROR::ModuleManager::append() : moduleInterface is contains !");
		}
	}
	
	public void removeAll(){
		pool.removeAllElements();
	}
	
	public ModuleInterface remove(ModuleInterface moduleInterface){
		if(pool.remove(moduleInterface)){
			return moduleInterface;
		}
		return null;
	}
	
	public Vector<ModuleInterface> getPool(){
		return pool;
	}
	
	/**
	 * ��������Ϣ�ַ����������.
	 */
	protected void modulesOnDraw(Canvas canvas, Paint paint){
		ModuleInterface moduleInterface = null;
		for(int i=0; i<pool.size(); i++){
			moduleInterface = pool.elementAt(i);
			moduleInterface.onDraw(canvas, paint);
			moduleInterface = null;
		}
	}
	
	/**
	 * �������¼��ַ����������.
	 */
	protected boolean modulesOnKey(int keyCode, KeyEvent keyEvent){
		boolean b = false;
		ModuleInterface moduleInterface = null;
		for(int i=pool.size()-1; i>=0; i--){
			moduleInterface = pool.elementAt(i);
			b = moduleInterface.onKey(keyCode, keyEvent);
			moduleInterface = null;
			if(b){
				break;
			}
		}
		return b;
	}
	
	/**
	 * ��������Ϣ�ַ����������.
	 */
	protected boolean modulesOnTouch(int event, int x, int y){
		
		boolean b = false;
		ModuleInterface moduleInterface = null;
		for(int i=pool.size()-1; i>=0; i--){
			moduleInterface = pool.elementAt(i);
			b = moduleInterface.onTouch(event, x, y);
			moduleInterface = null;
			if(b){
				break;
			}
		}
		return b;
	}
	
	
	/**
	 * ��������Ϣ�ַ����������.
	 */
	protected void modulesOnUpdate() {
		ModuleInterface moduleInterface = null;
		for(int i=0; i<pool.size(); i++){
			moduleInterface = pool.elementAt(i);
			moduleInterface.onUpdate();
			moduleInterface = null;
		}
	}
	
	
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	
	public void onDraw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		modulesOnDraw(canvas, paint);
	}
	
	
	public boolean onKey(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		return modulesOnKey(keyCode, keyEvent);
	}
	
	
	public boolean onTouch(int event, int x, int y) {
		// TODO Auto-generated method stub
		return modulesOnTouch(event, x, y);
	}
	
	
	public void onUpdate() {
		// TODO Auto-generated method stub
		modulesOnUpdate();
	}
	
	
}
