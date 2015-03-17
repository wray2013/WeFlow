package com.cmmobi.railwifi.utils;

import java.io.File;
import java.text.DecimalFormat;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class SpaceUtils {
	public static long getAvailSpaceOfSDCard() { 
		long availSpaceByte = 0;
		long blockSize;
		long blockCount;
		long availCount;
        String state = Environment.getExternalStorageState(); 
        if(Environment.MEDIA_MOUNTED.equals(state)) { 
            File sdcardDir = Environment.getExternalStorageDirectory(); 
            StatFs sf = new StatFs(sdcardDir.getPath()); 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                blockSize = sf.getBlockSizeLong(); 
                blockCount = sf.getBlockCountLong(); 
                availCount = sf.getAvailableBlocksLong(); 
            }else{
                blockSize = sf.getBlockSize(); 
                blockCount = sf.getBlockCount(); 
                availCount = sf.getAvailableBlocks(); 
            }

            Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB"); 
            Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB"); 
            availSpaceByte = availCount*blockSize;
        }    
        
        return availSpaceByte;
    } 
	
	public static long  getAvailSpaceOfRoot() { 
		long availSpaceByte = 0;
		long blockSize = 0;
		long blockCount = 0;
		long availCount = 0;
		
        File root = Environment.getRootDirectory(); 
        StatFs sf = new StatFs(root.getPath()); 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            blockSize = sf.getBlockSizeLong(); 
            blockCount = sf.getBlockCountLong(); 
            availCount = sf.getAvailableBlocksLong(); 
        }else{
            blockSize = sf.getBlockSize(); 
            blockCount = sf.getBlockCount(); 
            availCount = sf.getAvailableBlocks(); 
        }

        Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB"); 
        Log.d("", "可用的block数目：:"+ availCount+",可用大小:"+ availCount*blockSize/1024+"KB"); 
        availSpaceByte = availCount*blockSize;
        return availSpaceByte;
        
	}
	
	public static SpaceInfo getSpaceInfoOfSDCard() {
		
		SpaceInfo info = new SpaceInfo();
		info.totalSize = 0;
		info.availSize = 0;
		long availSpaceByte = 0;
		long blockSize = 0;
		long blockCount = 0;
		long availCount = 0;
        String state = Environment.getExternalStorageState(); 
        if(Environment.MEDIA_MOUNTED.equals(state)) { 
            File sdcardDir = Environment.getExternalStorageDirectory(); 
            StatFs sf = new StatFs(sdcardDir.getPath()); 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                blockSize = sf.getBlockSizeLong(); 
                blockCount = sf.getBlockCountLong(); 
                availCount = sf.getAvailableBlocksLong(); 
            }else{
                blockSize = sf.getBlockSize(); 
                blockCount = sf.getBlockCount(); 
                availCount = sf.getAvailableBlocks(); 
            }
            Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024/1024/1024+"GB"); 
            Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024/1024/1024+"GB"); 
            availSpaceByte = availCount*blockSize;
            info.totalSize = blockSize*blockCount;
            info.availSize = availCount*blockSize;
        }
        
		return info;
	}
	
	static DecimalFormat df1 = new DecimalFormat("0.#");
	public static String getSize(final long size) {
		String ret = "0B";
		long tempSize = size;
		int index = 0;
		while(tempSize / 1024 > 0) {
			tempSize = tempSize / 1024;
			index++;
		}
		if (index < 2) {
			ret = df1.format(size/1024.0) + "K";
		} else if (index < 3) {
			ret = df1.format(size/1024.0/1024.0) + "M";
		} else {
			ret = df1.format(size/1024.0/1024.0/1024.0) + "G";
		}
		return ret;
	}
	
	public static class SpaceInfo {
		
		long totalSize;
		long availSize;
		
		public String getTotalSize() {
			return getSize(totalSize);
		}
		
		public String getAvailSize() {
			return getSize(availSize);
		}
	}
    
}
