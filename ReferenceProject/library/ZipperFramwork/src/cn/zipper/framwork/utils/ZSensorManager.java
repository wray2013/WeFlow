package cn.zipper.framwork.utils;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import cn.zipper.framwork.core.ZSystemService;

public final class ZSensorManager {
	
	private static SensorManager sensorManager = ZSystemService.getSensorManager();
	
	private ZSensorManager(){
	}

	/**
	 * int	 TYPE_ACCELEROMETER	: 加速度;
	 * int	 TYPE_ALL : 所有类型，NexusOne默认为 加速度;
	 * int	 TYPE_GYROSCOPE	: 陀螺仪;
	 * int	 TYPE_LIGHT	: 光线感应;
	 * int	 TYPE_MAGNETIC_FIELD : 磁场;
	 * int	 TYPE_ORIENTATION : 定向（指北针）和角度;
	 * int	 TYPE_PRESSURE : 压力计;
	 * int	 TYPE_PROXIMITY : 距离;
	 * int	 TYPE_TEMPERATURE : 温度;
	 * @param SEL
	 * @param sensorType
	 * @param sensorDelay
	 */
	public static void registerListener(SensorEventListener SEL, int sensorType, int sensorDelay) {
		Sensor sensor = sensorManager.getDefaultSensor(sensorType);
		sensorManager.registerListener(SEL, sensor, sensorDelay);
	}
	
	public static void unregister(SensorEventListener SEL, int sensorType) {
		Sensor sensor = sensorManager.getDefaultSensor(sensorType);
		sensorManager.unregisterListener(SEL, sensor);
	}
	
	
}
