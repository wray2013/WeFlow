package cn.zipper.framwork.canvas;


public class RichVariable {
	
	private String name;
	private Object object;
	
	/**
	 * ��ֹ����;
	 */
	private RichVariable(String name, Object object){
		this.name = name;
		this.object = object;
	}
	
	public static RichVariable NEW(String name, Object object){
		return new RichVariable(name, object);
	}

}
