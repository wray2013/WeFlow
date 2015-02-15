package cn.zipper.framwork.core;

import java.lang.reflect.Field;

import android.util.Log;

public final class ZLog {
	
	private static final String Z_LOG_TAG = "Z_TAG";//保留字;
	private static final String Z_LOG_NULL = "Z_NULL";//保留字;
	private static final String Z_LOG_SPLIT = "▎";
	private static final String Z_LOG_ALERT = "★";
	private static boolean pushInStringBuffer;
	private static boolean needAlert;
	private static String tag = Z_LOG_TAG;
	private static StringBuffer stringBuffer = new StringBuffer();
	
	
	
	private ZLog() {
	}
	
	/**
	 * 设置是否将log内容缓存到StringBuffer;
	 * @param b
	 */
	public static void setStringBufferState(boolean b) {
		pushInStringBuffer = b;
	}
	
	/**
	 * 获取StringBuffer中存储的log信息;
	 * @return
	 */
	public static String getStringFromBuffer() {
		return stringBuffer.toString();
	}
	
	/**
	 * 输出对象中每个变量的值;
	 * @param object
	 */
	public static void printObject(Object object) {
		if (ZConfig.isDebug()) {
			
			try {
				if (object != null) {
					alert();
					e(object);
					Field[] fields = object.getClass().getDeclaredFields();
					
					if (fields != null) {
						for(int i = 0; i < fields.length; i++) {
							Field field = fields[i];
							boolean b = field.isAccessible();
							field.setAccessible(true);
							
//							if (field.getType().isArray()) {
//								field.getType(). 
//								printObject(field.get(object));
//							} else {
								e(field.getType().getSimpleName() + " " + field.getName() + " = " + field.get(object));
//							}
							
							field.setAccessible(b);
						}
					}
					
				} else {
					e(object);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 输出此函数调用处的调用栈;
	 */
	public static void printStackTrace() {
		if (ZConfig.isDebug()) {
			new Exception().printStackTrace();
		}
	}
	
	/**
	 * 输出此函数调用者所在的线程;
	 */
	public static void printThisThread() {
		Thread thread = Thread.currentThread();
		String string = "Thread : ID = " + thread.getId() + ", NAME = " + thread.getName();
		e(tag, string, true);
	}
	
	/**
	 * 输出字符串, 后面附带此函数调用者所在的线程;
	 * @param string
	 */
	public static void printThisThread(String string) {
		Thread thread = Thread.currentThread();
		String line = string +  " : Thread : ID = " + thread.getId() + ", NAME = " + thread.getName();
		e(tag, line, true);
	}
	
	/**
	 * 输出一行星号, ******;
	 */
	public static void printStarLine() {
		e(tag, null, null);
	}
	
	/**
	 * 星标; 此函数调用后, 下一行输出的log将被前缀星号★;
	 */
	public static void alert() {
		needAlert = true;
	}
	
	/**
	 * 修改默认tag, 默认tag为: "Z_TAG"
	 * @param tag
	 */
	public static void useOtherTag(String tag) {
		ZLog.tag = tag;
	}
	
	/**
	 * 恢复tag为默认tag: "Z_TAG"
	 */
	public static void useDefaultTag() {
		tag = Z_LOG_TAG;
	}
	
	/**
	 * 输出调用此函数的函数的名称;
	 */
	public static void e() {
		e(tag, Z_LOG_NULL, true);
	}
	
	/**
	 * 输出字符串;
	 * @param string
	 */
	public static void e(String string) {
		e(tag, string, true);
	}
	
	public static void e(boolean b) {
		e(tag, String.valueOf(b), true);
	}
	
	public static void e(byte value) {
		e(tag, "byte: " + String.valueOf(value), true);
	}
	
	public static void e(short value) {
		e(tag, "short: " + String.valueOf(value), true);
	}
	
	public static void e(int value) {
		e(tag, "int: " + String.valueOf(value), true);
	}
	
	public static void e(long value) {
		e(tag, "long: " + String.valueOf(value), true);
	}
	
	public static void e(float value) {
		e(tag, "float: " + String.valueOf(value), true);
	}
	
	public static void e(double value) {
		e(tag, "double: " + String.valueOf(value), true);
	}
	
	/**
	 * 输出对象的值;
	 * @param c
	 */
	public static void e(Object c) {
		String string = "Object = ";
		if (c != null) {
			string = string + c;
		} else {
			string = string + "NULL!";
		}
		e(tag, string, true);
	}
	
	
	public static void e(String string, boolean useSimpleName) {
		e(tag, string, useSimpleName);
	}
	
	private static void e(String tag, String string, Boolean useSimpleName) {
		if (ZConfig.isDebug() || pushInStringBuffer) {
			
			String line = null;
			if (useSimpleName == null) {
				line = "*****************************************************************************************************";
			} else if (string == Z_LOG_NULL){
				line = getMethodTrace(useSimpleName) + ";";
			} else {
				String alert =  needAlert ? Z_LOG_ALERT : "  ";
				line = getMethodTrace(useSimpleName) + Z_LOG_SPLIT + alert +"[ " + string + " ]";
				needAlert = false;
			}
			line = Z_LOG_SPLIT + line;
			if (ZConfig.isDebug()) {
				Log.e(tag, line);
			}
			if (pushInStringBuffer) {
				stringBuffer.append(line + "\n");
			}
		}
	}
	
	private static String getMethodTrace(boolean useSimpleName) {
		StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[3];
		String temp = stackTraceElement.getClassName();
		String string = (useSimpleName ? getSimpleName(temp) : temp) + "." + stackTraceElement.getMethodName() + "()";
		stackTraceElements = null;
		stackTraceElement = null;
		temp = null;
		return string;
	}
	
	private static String getSimpleName(String string) {
		string = string.substring(string.lastIndexOf(".") + 1);
		string = string.replace("$", ":");
		return string;
	}
	
}
