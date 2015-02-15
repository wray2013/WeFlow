package com.cmmobi.looklook.common.utils;

import java.util.ArrayList;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-11-22
 * @desc   : TODO
 */
public class Params {

	public final ArrayList<NameValue> nameValueArray = new ArrayList<NameValue>();
	
	public static class NameValue{
		public final String name;
		public final Object value;
		public NameValue(String name,Object value){
			this.name = name;
			this.value = value;
		}
	}
	
	public Params put(String name,String value){
		appendToParamsArray(name,value);
		return this;
	}
	
	public Params put(String name,int value){
		appendToParamsArray(name,value);
		return this;
	}

	public Params put(String name,boolean value){
		appendToParamsArray(name,value);
		return this;
	}

	public Params put(String name,float value){
		appendToParamsArray(name,value);
		return this;
	}

	public Params put(String name,long value){
		appendToParamsArray(name,value);
		return this;
	}

	public Params put(String name,double value){
		appendToParamsArray(name,value);
		return this;
	}
	
	private Params appendToParamsArray(String name,Object value){
		if(value != null && value != null && !"".equals(name) && !"".equals(value) ){
			nameValueArray.add(new NameValue(name, value));
		}
		return this;
	}
	
	public static Params build(){
		return new Params();
	};
}
