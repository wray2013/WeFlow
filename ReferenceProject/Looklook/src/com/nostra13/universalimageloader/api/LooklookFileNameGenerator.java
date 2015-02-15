package com.nostra13.universalimageloader.api;

import com.cmmobi.looklook.common.utils.MD5;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

public class LooklookFileNameGenerator implements FileNameGenerator {
	
	private String uid;

	public LooklookFileNameGenerator(String uid){
		this.uid = uid;
	}

	@Override
	public String generate(String imageUri) {
		// TODO Auto-generated method stub
		String key = MD5.encode((uid+imageUri).getBytes()) + ".jpg";
		return key;
	}

}
