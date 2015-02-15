package cn.zipper.framwork.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import cxx.oxy.engine.core.ModuleInterface;
import cxx.oxy.engine.game.Camera;
import cxx.oxy.engine.game.Scene;
import cxx.oxy.engine.graphics.XPoint;
import cxx.oxy.engine.graphics.XPoint3D;
import cxx.oxy.engine.graphics.XRect;

public class PhysicsObject implements ModuleInterface{
	
	public static final float GRAVITY = 1.5f;
	
	private ZRect rect;
	private XPoint3D point;//�����ڵ�ͼ�ϵ����;
	private XPoint screenPoint;//��������Ļ�ϵ����;(��������λ�ú������ڵ�ͼ�ϵ�λ�õ��Ӽ���ó�)
	private XPoint3D speed3D;//3D�ٶ�;
	private float weight;//��;
	
	
	public PhysicsObject(){
		init();
	}
	
	
	public void init() {
		// TODO Auto-generated method stub
		rect = new ZRect();
		point = new XPoint3D();
		point.setX(Integer.MIN_VALUE);//��ֹ���屻new��4��ʱ���������Ļ��, ����λ�ô���;
		point.setY(Integer.MIN_VALUE);//��ֹ���屻new��4��ʱ���������Ļ��, ����λ�ô���;
		point.setZ(0);//��ֹ���屻new��4��ʱ���������Ļ��, ����λ�ô���;
		speed3D = new XPoint3D();
		screenPoint = new XPoint();
		weight = 1;
	}
	
	/**
	 * ��ʹ��getPoint()��Ϊ������, ��Ϊ�����кܶ෽��;
	 * @return
	 */
	public XPoint3D point(){
		return point;
	}
	
	public XPoint screenPoint(){
		return screenPoint;
	}
	
	public ZRect rect(){
		return rect;
	}
	
	public void setWeight(float weight){
		this.weight = weight;
	}
	
	public float getWeight(){
		return weight;
	}
	
	public void forceX(float force){
		float accelerationX = force/weight;
		speed3D.moveX(accelerationX);
	}
	
	public void forceY(float force){
		float accelerationY = force/weight;
		speed3D.moveY(accelerationY);
	}
	
	public void forceZ(float force){
		float accelerationZ = force/weight;
		speed3D.moveZ(accelerationZ);
	}
	
	protected XPoint getScreenPoint() {
		return screenPoint;
	}
	
	
	public void onDraw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
	}
	
	public boolean onKey(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean onTouch(int event, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * ģ������;
	 */
	private void Damping(float DampingX, float DampingY, float DampingZ){
		if(speed3D.getX() > 0){
			forceX(-DampingX);
		}else if(speed3D.getX() < 0){
			forceX(DampingX);
		}
		
		if(speed3D.getY() > 0){
			forceY(-DampingY);
		}else if(speed3D.getY() < 0){
			forceY(DampingY);
		}
		
		if(speed3D.getZ() > 0){
			forceZ(-DampingZ);
		}else if(speed3D.getZ() < 0){
			forceZ(DampingZ);
		}
	}
	
	public void onUpdate() {
		// TODO Auto-generated method stub
		forceZ(-GRAVITY);//Z��һֱ�ܵ���f;
		point.moveX(speed3D.getX());
		point.moveY(speed3D.getY());
		point.moveZ(speed3D.getZ());
		
//		point.moveX(RandomUtil.rand(-1, 1));
//		point.moveY(RandomUtil.rand(-1, 1));
		
		if(point.getZ() <= 0){
			point.setZ(0);
			speed3D.setZ(0);
		}
		//��ʱ����������������Ļ���;
		Camera camera = Scene.getInstanse().getCamera();
		float x = point.getX() - camera.getObserverRect().getX();
		float y = point.getY() - camera.getObserverRect().getY();
		float z = point.getZ();
		screenPoint.setX(x);
		screenPoint.setY(y - z);
		
		Damping(1, 1, 1);
		
//		System.out.println("x = " + point.getX());
//		System.out.println("y = " + point.getY());
	}
	
}
