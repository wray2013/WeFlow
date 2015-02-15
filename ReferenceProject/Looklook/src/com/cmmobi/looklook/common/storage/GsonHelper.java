package com.cmmobi.looklook.common.storage;

import com.google.gson.Gson;

public class GsonHelper {
	private static GsonHelper ins = null;
	
	
	private Gson gson;
	
	private GsonHelper(){
		gson = new Gson();
	}
	
	public static GsonHelper getInstance(){
		if(ins==null){
			ins = new GsonHelper();
		}
		
		return ins;
	}
	
	public String getString(Object obj){
		return gson.toJson(obj);
	}
	
	public Object getObject(String str, Class<?>cls){
		Object ret = null;
		if(str!=null){
			try{
				ret = gson.fromJson(str, cls);
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		
		return 	ret;
	}


}
