package cn.zipper.framwork.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;

public final class ZFileSystem {
	
	private ZFileSystem() {
	}
	
	public static File getFile(File file, String name) {
		return new File(file, name);
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	public static boolean delFile(String sPath) {
	    boolean flag = false;  
	    File file = new File(sPath);
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {
	        file.delete();  
	        flag = true;  
	    }
	    
	    return flag;  
	} 
	
	public static boolean isFileExists(String filepath) {
		if (filepath == null || "".equals(filepath)) {
			return false;
		}
		File file = new File(filepath);
		return file.exists();
	}
	
	public static void renameTo(String path, String newPath) {
		if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(newPath)) {
			File file = new File(path);
			 if (file.exists()) {
				 file.renameTo(new File(newPath));
			 }
		}
	}
	
	public static File getFile(String path, String name) {
		return new File(path, name);
	}
	
	public static String getFullPath(String path, String name) {
		return getFile(path, name).getAbsolutePath();
	}
	
	public static String getFullPath(File path, String name) {
		return getFile(path, name).getAbsolutePath();
	}
	
	public static long getAvailableStorageSize(File file) {
		return getAvailableStorageSize(file.getAbsolutePath());
	}
	
	public static long getAvailableStorageSize(String path) {
		StatFs stat = new StatFs(path);
	    return stat.getBlockSize() * stat.getAvailableBlocks();
	}
	
	public static void copy(String original, String target) {
		if (original != null && target != null) {
			copy(new File(original), new File(target));
		}
	}
	
	public static void copy(File original, File target) {
		
		if (original != null && target != null && original.exists() && !target.exists() && !original.equals(target)) {
			try {
				target.getParentFile().mkdirs();
				boolean b = target.createNewFile();
				
				if (b) {
					FileInputStream inputStream = new FileInputStream(original);
					FileOutputStream outputStream = new FileOutputStream(target);
					
					byte[] bytes = new byte[1024 * 128];
					int count = 0;
					
					while ((count = inputStream.read(bytes)) > 0) {
						outputStream.write(bytes, 0, count);
					}
					
					inputStream.close();
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static final class ZInternalFile {
		
		private ZInternalFile() {
		}
		
		public static FileOutputStream getFileOutputStream(String name, int mode) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = ZApplication.getInstance().openFileOutput(name, mode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return fileOutputStream;
		}
		
		public static FileInputStream getFileInputStream(String name) {
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = ZApplication.getInstance().openFileInput(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return fileInputStream;
		}
		
		public static void delete(String name) {
			ZApplication.getInstance().deleteFile(name);
		}
		
		public static File getFilesDir() {
			return ZApplication.getInstance().getFilesDir();
		}
		
		public static File getFileStreamPath(String name) {
			return ZApplication.getInstance().getFileStreamPath(name);
		}
		
		public static File getCacheDir() {
			return ZApplication.getInstance().getCacheDir();
		}
		
		public static File getDir(String name, int mode) {
			return ZApplication.getInstance().getDir(name, mode);
		}
		
		public static String[] getFileList() {
			return ZApplication.getInstance().fileList();
		}
	}
	
	
	public static final class ZExternalFile {
		
		private ZExternalFile() {
		}
		
		public static String getExternalStorageState() {
			return Environment.getExternalStorageState();
		}
		
		public static boolean isExternalStorageReady() {
			return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		}
		
		public static boolean isExternalStorageReadOnly() {
			return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		}
		
		public static File getExternalStorageDirectory() {
			return Environment.getExternalStorageDirectory();
		}
		
		public static File getExternalStoragePublicDirectory(String type) {
			return Environment.getExternalStoragePublicDirectory(type);
		}
		
		public static File getExternalFilesDir(String type) {
			return ZApplication.getInstance().getExternalFilesDir(type);
		}
		
		public static File getExternalCacheDir() {
			return ZApplication.getInstance().getExternalCacheDir();
		}
	}
	
}
