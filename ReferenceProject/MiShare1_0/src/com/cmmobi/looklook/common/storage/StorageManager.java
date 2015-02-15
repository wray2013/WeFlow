package com.cmmobi.looklook.common.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.sql.MyDirayDeserializer_sql;
import com.cmmobi.looklook.common.sql.MyDiraySerializer_sql;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.info.profile.MediaMapping;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

/**
 * @author zhangwei
 */
public class StorageManager {
	public static final String CHACHE_PREFIX = "storage_";

	private static final String TAG = "StorageManager";
	private static StorageManager instance;
	//private Map<String, StorageValue> cache;
	private static Context context;
	private Gson gson;
	

	private class CacheFilter implements FilenameFilter {

		public boolean isCache(String file) {
			if (file.toLowerCase(Locale.ENGLISH).startsWith(CHACHE_PREFIX)) {
				return true;
			} else {
				return false;
			}
		}

		public boolean accept(File dir, String fname) {
			return (isCache(fname));

		}

	}


	
	private StorageManager(Context c) {
		context = c;
		//cache = new HashMap<String, StorageValue>();
		
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(GsonResponse3.MyDiary.class, new MyDirayDeserializer_sql());
		gsonBuilder.registerTypeAdapter(GsonResponse3.MyDiary.class, new MyDiraySerializer_sql());
		//gsonBuilder.registerTypeAdapter(ConcurrentLinkedHashMap.class, new MyInstanceCreator());

		gson = gsonBuilder./*setPrettyPrinting().*/create();
		
		//load();
	}
	
//	private class MyInstanceCreator implements InstanceCreator<ConcurrentLinkedHashMap<String, MediaValue>> {
//		public ConcurrentLinkedHashMap<String, MediaValue> createInstance(Type type) {
//			ConcurrentLinkedHashMap<String, MediaValue> map = new ConcurrentLinkedHashMap.Builder<String, MediaValue>()
//					.maximumWeightedCapacity(Constant.MEDIA_CACHE_LIMIT) // 1GB, internal storage, not memory
//				    .weigher(MediaMapping.memoryUsageWeigher)
//				    .listener(MediaMapping.listener)
//				    .build();
//			return map;
//		}
//		
//	}

/*	private void load() {
		Log.e(TAG, "CacheManager load");
		FilenameFilter filter = new CacheFilter();
		File[] filelist = context.getFilesDir().listFiles(filter);
		for (File f : filelist) {
			cache.put(f.getName(),
					new StorageValue(f.getName(), (int) f.length()));
		}
	}*/
	
	public synchronized static StorageManager getInstance(){
		return getInstance(MainApplication.getAppInstance());//MainApplication.getAppInstance()
	}

	private synchronized static StorageManager getInstance(Context c) {
		//Context context = null;
		if (c != null) {
			context = c;
		} else {
			context = MainApplication.getAppInstance();
		}

		if (instance == null) {
			instance = new StorageManager(context);
		}

		return instance;
	}

	/**
	 * @return 被Cache的元数据信息
	 */
	public synchronized String getItem(String uri) {
		String key = MD5.encode(CHACHE_PREFIX, uri.getBytes());

		// Read the created file and display to the screen
		try {
			FileInputStream mInput = context.openFileInput(key);
			int len = 0;
			byte[] data = new byte[1024];

			ByteArrayBuffer ba = new ByteArrayBuffer(1024);
			while ((len = mInput.read(data)) != -1) {
				ba.append(data, 0, len);
			}
			
			String tmp = decompress(ba.toByteArray());
			//String tmp = new String(ba.toByteArray());

			mInput.close();

			return tmp;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	
	}

	/**
	 * @return 被Cache的元数据信息
	 */
	public synchronized Object getItem(String uri, Class<?> cls) {
		String jsonStr = getItem(uri);
		Object object = null;
		try{
			if (jsonStr != null) {
				object = gson.fromJson(jsonStr, cls);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

		return object;
	}
	
	public synchronized Object getItem(String uri, Type type) {
		String jsonStr = getItem(uri);
		Object object = null;
		try{
			if (jsonStr != null) {
				object = gson.fromJson(jsonStr, type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}


		return object;
	}

	/**
	 * 
	 * @param uri 要查找的资源uri
	 * @param objStr 将json String作为内容写入内存储(overwrite)
	 */
	public synchronized StorageValue  putItem(String uri, String objStr) {
		String key = MD5.encode(CHACHE_PREFIX, uri.getBytes());
		FileOutputStream mOutput = null;
		StorageValue result = null;
		FileChannel fcout;
		try {
			File DIR = context.getFilesDir();
			String file_path = DIR.getAbsolutePath() + "/" + key;
			File file = new File(file_path);
			
			// overwrite
			//---------------- normal way:
/*			mOutput = context.openFileOutput(key, Activity.MODE_PRIVATE);
			mOutput.write(objStr.getBytes());*/
			
/*			mOutput = new FileOutputStream(file, false);
			mOutput.write(objStr.getBytes());
			mOutput.close();*/

			
			//---------------- nio way:
			mOutput = new FileOutputStream(file, false);
			fcout = mOutput.getChannel();
			ByteBuffer wBuffer = ByteBuffer.wrap(compress(objStr));
			//ByteBuffer wBuffer = ByteBuffer.wrap(objStr.getBytes());
			fcout.write(wBuffer);
			fcout.close();
			
			//---------------------------------
			mOutput.close();
			//result = cache.put(key, new StorageValue(key, objStr.getBytes().length));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param uri 要插入的资源uri
	 * @param object json Object
	 * @param cls json对象类型
	 */
	public synchronized StorageValue putItem(String uri, Object object, Class<?> cls) {


		String jsonStr = null;
		jsonStr = gson.toJson(object, cls);
		
		if (jsonStr != null) {
			//Log.e(TAG, "putItem:" + jsonStr);
			return putItem(uri, jsonStr);
		} else {
			return null;
		}
	}
	
	/**
	 * 压缩字符串为 byte[] 储存可以使用new sun.misc.BASE64Encoder().encodeBuffer(byte[] b)方法
	 * 保存为字符串
	 * 
	 * @param str 压缩前的文本
	 * @return
	 */
	public static final byte[] compress(String str) {
		if (str == null)
			return null;

		byte[] compressed;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;

		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes());
			zout.closeEntry();
			compressed = out.toByteArray();
		} catch (IOException e) {
			compressed = null;
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return compressed;
	}

	/**
	 * 将压缩后的 byte[] 数据解压缩
	 * 
	 * @param compressed 压缩后的 byte[] 数据
	 * @return 解压后的字符串
	 */
	public static final String decompress(byte[] compressed) {
		if (compressed == null)
			return null;

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			ZipEntry entry = zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}

	/**
	 * 
	 * @param uri 要删除的资源uri
	 */
	public synchronized void deleteItem(String uri) {
		String key = MD5.encode(CHACHE_PREFIX, uri.getBytes());
		try{
			context.deleteFile(key);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

/*	public synchronized void cleanAll() {}*/

}