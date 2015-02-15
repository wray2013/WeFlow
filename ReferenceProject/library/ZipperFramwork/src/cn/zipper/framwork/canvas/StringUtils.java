package cn.zipper.framwork.canvas;

import java.util.Vector;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

public final class StringUtils {
	
	/**
	 * ȡ����߶�;
	 * @param paint
	 * @return
	 */
	public static int getFontHeight(Paint paint){
		FontMetrics fontMetrics = paint.getFontMetrics();
		return (int) Math.ceil(fontMetrics.descent - fontMetrics.top) + 2;
	}
	
	/**
	 * ȡ�ַ���;
	 * @param paint
	 * @param string
	 * @return
	 */
	public static int getStringWidth(Paint paint, String string){
		if(string == null){
			string = "";
		}
		return (int) Math.ceil(paint.measureText(string));
	}
	
	
	public static void trimStringArray(String[] array){
		if(array != null){
			for(int i=0; i<array.length; i++){
				array[i] = array[i].trim();
			}
		}
	}
	
	public static String[] split(int firstLineWidth, int otherLineWidth, String text, Paint paint) {
		
		String[] strings = null;
		if(text != null && paint != null && otherLineWidth >= getStringWidth(paint, "��")){
			if(getStringWidth(paint, text) > firstLineWidth || text.indexOf("\n") > -1){
				Vector<String> vector = new Vector<String>();
				int lastFlag = 0;
				int index = 0;
				int tempWidth = 0;
				int tempCharWidth = 0;
				String string = null;
	 			while(index < text.length()){
					if(true){//isVisibleChar(text.charAt(index))
//						tempCharWidth = getCharWidth(text.charAt(index), font);
						tempCharWidth = getStringWidth(paint, text.substring(index, index + 1));
						if(tempWidth + tempCharWidth <= firstLineWidth){
							tempWidth += tempCharWidth;
							index ++;
							if(index == text.length()){
								string = text.substring(lastFlag, index);
								if(string.length() > 0){
									vector.addElement(string);
								}
								string = null;
							}
						}else{
							string = text.substring(lastFlag, index);
							if(string.length() > 0){
								vector.addElement(string);
							}
							string = null;
							lastFlag = index;
							tempWidth = 0;
							firstLineWidth = otherLineWidth;
						}
					} else if(text.charAt(index) == '\n'){
						string = text.substring(lastFlag, index);
						if(string.length() > 0){
							vector.addElement(string);
						}
						string = null;
						lastFlag = index;
						tempWidth = 0;
						firstLineWidth = otherLineWidth;
						index ++;
					} else{
						index ++;
					}
				}
				strings = new String[vector.size()];
				for(int i=0; i< vector.size(); i++){
					strings[i] = vector.elementAt(i);
				}
				vector.removeAllElements();
				vector = null;
			}else{
				strings = new String[1];
				strings[0] = text;
			}
		}else{
			
		}
		return strings;
	}
	
	

}
