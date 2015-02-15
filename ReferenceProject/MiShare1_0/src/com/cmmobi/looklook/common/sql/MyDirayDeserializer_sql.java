package com.cmmobi.looklook.common.sql;

import java.lang.reflect.Type;

import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.storage.SqliteDairyManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MyDirayDeserializer_sql implements JsonDeserializer<GsonResponse3.MyDiary> {

	@Override
	public GsonResponse3.MyDiary deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		// TODO Auto-generated method stub
		String dirayuuid = json.getAsJsonPrimitive().getAsString();
/*		SqliteDairyManager sdm = SqliteDairyManager.getInstance();
		return sdm.getDiary(dirayuuid);*/
		
		return new MyDiary(dirayuuid);
	}

}