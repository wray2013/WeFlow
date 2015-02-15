package com.cmmobi.railwifi.cache;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import com.cmmobi.common.tools.MD5Util;

import android.content.Context;

public class GifFileCache {
	private Map<String, String> map;
	private String cache_folder_name;
	private File folder;
	private String postFixName;

	public GifFileCache(Context context, String cache_folder_name, String postFixName) {
		// TODO Auto-generated constructor stub
		this.cache_folder_name = cache_folder_name;
		this.map = new HashMap<String, String>();
		this.postFixName = postFixName;
		
		folder = new File(cache_folder_name);
		if(!folder.exists()){
			if(folder.isFile()){
				folder.delete();
			}
			folder.mkdirs();
		}
	}

	public File getFile(String url) {
		// TODO Auto-generated method stub
		String path = map.get(url);
		if(path==null){
			String md5_str = MD5Util.getMD5String(url);
			path = cache_folder_name + "/" + md5_str + ".gif";
			map.put(url, path);
		}
		
		return new File(path);
	}

	public void clear() {
		// TODO Auto-generated method stub
		if(folder!=null && folder.isDirectory()){
			for(File f : folder.listFiles(new GifFileNameFilter(postFixName))){
				f.delete();
			}
		}
		
		map.clear();
	}
	
	public class GifFileNameFilter implements FilenameFilter{
		private String postFixName;

		public GifFileNameFilter(String postFixName) {
			// TODO Auto-generated constructor stub
			this.postFixName = postFixName;
		}

		@Override
		public boolean accept(File dir, String filename) {
			// TODO Auto-generated method stub
			if(filename.toLowerCase().endsWith(postFixName)){
				return true;
			}
			return false;
		}
		
	}

}
