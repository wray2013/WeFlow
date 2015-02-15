package com.cmmobi.looklook.common.sql;

import java.lang.reflect.Type;

import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MyDiraySerializer_sql implements JsonSerializer<GsonResponse3.MyDiary>{

	@Override
	public JsonElement serialize(GsonResponse3.MyDiary src, Type typeOfSrc,
			JsonSerializationContext context) {
		// TODO Auto-generated method stub
		return new JsonPrimitive(src.toString());
	}

}

