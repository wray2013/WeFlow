package cn.zipper.framwork.utils;

import java.util.Vector;


/**
 * �Զ���ֵ��������;
 */
public class TrendGroup {
	
	public static final int MODE_AVERAGE = 0;// ƽ���
	public static final int MODE_APPROACH = 1;// ����
	
	public static final int LOOP_MODE_FOREVER = -1;// ��Զѭ��
	public static final int LOOP_MODE_DONT_LOOP = 0;// ��ѭ��
	
	private Vector<Float> buffer;
	private TrendGroupListener TGL;
	private int mode;
	private int index;//�ӵ�0��ڵ㿪ʼ;
	private int tempIndex;
	/**
	 * �ټ��ϵ���ѭ��!!!!!!!!!!!!!!!!!!!
	 * ÿ��ڵ����ʱӦ��֪ͨ, ���߽��ڵ�����봥������������ĳ�ڵ�jb;
	 */
	private int loopCounts;// -1: ��Զѭ��; 0: ��ѭ��; 1-max: ѭ������;
	private int loopTimes;//��4��¼ѭ������;
	private float lastTempValue;
	private boolean stop;
	
	
	public TrendGroup(int mode, int loopCounts, TrendGroupListener TGL){
		buffer = new Vector<Float>();
		this.mode = mode;
		this.loopCounts = loopCounts;
		this.TGL = TGL;
	}
	
	/**
	 * 
	 * @param object: Ҫ����Ķ���(��Ҫ����ֵ);
	 * @param differenceTimes: ��ֵ����;
	 */
	public void appendNode(float f, int differenceTimes){
		buffer.add(f);
		if(differenceTimes <= 0){
			differenceTimes = 1;
		}
		buffer.add((float)differenceTimes);
	}
	
	public void setLastNode(float f){
		buffer.add(f);
	}
	
	public void removeAllNode(){
		buffer.removeAllElements();
		stop(true);
	}
	
	public void reset(boolean triggerOnTrendGroupLoopEvent){
		index = 0;
		tempIndex = 0;
		stop = false;
		if(TGL != null && triggerOnTrendGroupLoopEvent){
			TGL.onTrendGroupLoop(this);
		}
	}
	
	public void stop(boolean triggerOnTrendGroupStopEvent){
		stop = true;
		if(TGL != null && triggerOnTrendGroupStopEvent){
			TGL.onTrendGroupStop(this);
		}
	}
	
	public int getLoopTimes(){
		return loopTimes;
	}
	
	public float runToNextValue(){
		if(!stop){
			
			float currentNodeStartValue = buffer.elementAt(index);
			float currentNodeTimes = (buffer.elementAt(index + 1).floatValue());
			float currentNodeEndValue = buffer.elementAt(index + 2);
			
			boolean b = false;
			
			switch (mode) {
			case MODE_AVERAGE://ƽ���;
				
				lastTempValue = currentNodeStartValue + tempIndex * ((currentNodeEndValue - currentNodeStartValue)/currentNodeTimes);
				tempIndex ++;
				if(tempIndex > currentNodeTimes){
					b = true;
				}
				break;
				
			case MODE_APPROACH://����;
				
				float width = currentNodeEndValue - currentNodeStartValue;
				float temp = ((currentNodeTimes - 1)/currentNodeTimes);// (N-1)/N ����;
				lastTempValue = currentNodeStartValue + width - (width * (float)Math.pow(temp, tempIndex)) + 1.0f/currentNodeTimes;
				if(Math.abs(lastTempValue - currentNodeEndValue) < 1){
					b = true;
				}
				tempIndex ++;
				break;
			}
			
			if(b){
				tempIndex = (int)currentNodeTimes;
				index += 2;
				if(index > buffer.size() - 3){
					index = buffer.size() - 3;
					if(loopCounts != 0){//ѭ��;
						loopTimes ++;
						reset(true);
						if(loopCounts > 0){
							loopCounts --;
						}
					}else{//����;
						stop(true);
					}
				}else{
					tempIndex = 0;
				}
			}
		}
		return lastTempValue;
	}
	
}
