package cn.zipper.framwork.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 等比缩放器, 用来计算等比例缩放关系;
 * 
 * 用例: 将bitmapA的尺寸等比缩放到bitmapB的尺寸;
 * 
 * Model originalModel = new Model(); // 原始模型(bitmapA); 
 * originalModel.put(Model.W, bitmapA.getWidth()); // 添加参数W; 
 * originalModel.put(Model.H, bitmapA.getHeight()); // 添加参数H;
 * 
 * Model standardModel = new Model(); // 标准模型(bitmapB); 
 * standardModel.put(Model.W, bitmapB.getWidth()); // 添加参数W; 
 * standardModel.put(Model.H, bitmapB.getHeight()); // 添加参数H;
 * 
 * // 将原始模型等比缩放到标准模型 (两个模型的参数个数和参数名要一致); 
 * ZUniformScaler.scale(originalModel, standardModel, ScaleType.IN/ScaleType.OUT);
 * 
 * 调用了ZUniformScaler.scale()之后, 原始模型originalModel的各项参数已经是缩放后的值;
 * double width = originalModel.get(Model.W);
 * double height = originalModel.get(Model.H);
 * 
 * @author Sunshine
 * 
 */
public final class ZUniformScaler {
	
	/**
	 * 缩放类型;
	 * IN: 将原始模型缩放到标准模型的内部,所有属性的值都不超过标准模型 (撑起标准模型);
	 * OUT: 将原始模型缩放到标准模型的外部,所有属性的值都不小于标准模型 (包裹标准模型);
	 */
	public static enum ScaleType {IN, OUT};
	
	
	public static final class Model {
		
		//方便使用的默认参数名 (可以在外部任意指定其他名称);
		public static final String A = "A";
		public static final String B = "B";
		public static final String C = "C";
		public static final String D = "D";
		public static final String E = "E";
		public static final String F = "F";
		public static final String G = "G";
		public static final String H = "H";
		public static final String I = "I";
		public static final String J = "J";
		public static final String K = "K";
		public static final String L = "L";
		public static final String M = "M";
		public static final String N = "N";
		public static final String O = "O";
		public static final String P = "P";
		public static final String Q = "Q";
		public static final String R = "R";
		public static final String S = "S";
		public static final String T = "T";
		public static final String U = "U";
		public static final String V = "V";
		public static final String W = "W";
		public static final String X = "X";
		public static final String Y = "Y";
		public static final String Z = "Z";
		
		private HashMap<String, Double> map;
		
		public Model() {
			map = new HashMap<String, Double>();
		}
		
		public void put(String name, double d) {
			map.put(name, d);
		}
		
		public double get(String name) {
			return map.get(name);
		}
	}
	
	private ZUniformScaler() {
	}
	
	/**
	 * 缩放originalModel, 使其接近standardModel;
	 * @param <T>
	 * @param originalModel
	 * @param standardModel
	 * @param type
	 * @return
	 */
	public static Model scale(Model originalModel, Model standardModel, ScaleType type) {
		Map.Entry<String, Double> entry = null;
		
		switch (type) {
		case IN:
			entry = findMinEntry(standardModel);
			break;
			
		case OUT:
			entry = findMaxEntry(standardModel);
			break;
		}
		
		double standardValue = entry.getValue();
		double originalValue = originalModel.map.get(entry.getKey());
		double scale = standardValue / originalValue;
		scaleModel(originalModel, scale);
		
		return originalModel;
	}
	
	private static Map.Entry<String, Double> findMinEntry(Model model) {
		Map.Entry<String, Double> entry = null;
		double min = Double.MAX_VALUE;
		
		Set<Map.Entry<String, Double>> set = model.map.entrySet();
		for (Map.Entry<String, Double> temp : set) {
			double value = temp.getValue();
			if (value <= min) {
				min = value;
				entry = temp;
			}
		}
		
		return entry;
	}
	
	private static Map.Entry<String, Double> findMaxEntry(Model model) {
		Map.Entry<String, Double> entry = null;
		double max = Double.MIN_VALUE;
		
		Set<Map.Entry<String, Double>> set = model.map.entrySet();
		for (Map.Entry<String, Double> temp : set) {
			double value = temp.getValue();
			if (value >= max) {
				max = value;
				entry = temp;
			}
		}
		
		return entry;
	}
	
	private static void scaleModel(Model model, double scale) {
		Set<Map.Entry<String, Double>> set = model.map.entrySet();
		for (Map.Entry<String, Double> temp : set) {
			temp.setValue(temp.getValue() * scale);
		}
	}
	
}
