package com.cmmobi.looklook.httpproxy;

/**
 * Author:Ray
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.zipper.framwork.core.ZLog;

import com.google.gson.Gson;

import android.util.Log;

public class ProxyTempFile {

	private static final String TAG = "ProxyTempFile";

	private String tmpfileName;
	private File cfgfile;
	private boolean isComleteFile = false;
	
	private static String TMP_SUFFIX = ".tmp";
	private static String CFG_SUFFIX = ".cmmobi";
	
	private Gson gson;
	
	private BlockMap bMap;
	
	public enum BlockPosition {
		BLOCK_POS_LEFT, 
		BLOCK_POS_INSIDE,
		BLOCK_POS_RIGHT
	}
	
	public static class tmpBlock {
		public int range;
		public int size;
		// return 1:left out 2:inside 3:right out
		public BlockPosition isInside(int point) {
			BlockPosition ret;
			if(point < this.range) {
				ret = BlockPosition.BLOCK_POS_LEFT;
			} else if(point >= this.range && point <= this.range + this.size) {
				ret = BlockPosition.BLOCK_POS_INSIDE;
			} else {
				ret = BlockPosition.BLOCK_POS_RIGHT;
			}
			return ret;
		}
	}
	
	public class BlockMap {
		/** 需要保证每个下载块无重叠 */
		public List<tmpBlock> downMap = new ArrayList<tmpBlock>();
	}

	
	public ProxyTempFile(String filePath, long fileSize) {
		this.gson = new Gson();
		if (filePath.endsWith(TMP_SUFFIX)) {
			File tfile = new File(filePath);
			this.tmpfileName = filePath;
			this.cfgfile = new File(filePath.replace(TMP_SUFFIX, CFG_SUFFIX));
			if (!tfile.exists()) {
				bMap = new BlockMap();
				createTMPFiles(filePath, fileSize);
			} else {
				try {
					String json = readCfgFile();
					Log.d(TAG, "json=" + json);
					bMap = this.gson.fromJson(json, BlockMap.class);
					ZLog.printObject(bMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			Log.e(TAG, "Invalid File Suffix [" + filePath.substring(filePath.lastIndexOf(".")) + "]");
		}
	}
	
	// 默认请求长度为全文件
	public tmpBlock isInsideRange(int range) {
		if(bMap.downMap != null && bMap.downMap.size() > 0) {
			for(Iterator<tmpBlock> it = bMap.downMap.iterator(); it.hasNext();) {
				tmpBlock tmpBlock = it.next();
				if(tmpBlock.isInside(range) == BlockPosition.BLOCK_POS_INSIDE) {
					return tmpBlock;
				}
			}
		}
		return null;
	}
	
	public void writeTmpFile(int offset, byte[] data) throws IOException {
		if (new File(tmpfileName).exists()) {
			new FileWriteThread(offset, data, tmpfileName).start();
			MergeMap(offset, data.length);
		}
	}
	
	private void createTMPFiles(String filePath, long fileSize) {
		RandomAccessFile tfile = null;
		try {
			tfile = new RandomAccessFile(filePath, "rw");
			tfile.setLength(fileSize);
			this.cfgfile.createNewFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (tfile != null) {
					tfile.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void MergeMap(int range, int size) {
//		ZLog.printObject(bMap.downMap);
//		System.out.println("=========================");
//		System.out.println("range=" + range + ";size=" + size);
//		downMap
		if (bMap.downMap != null && bMap.downMap.size() == 0) {
			tmpBlock newBlock = new tmpBlock();
			newBlock.range = range;
			newBlock.size = size;
			bMap.downMap.add(newBlock);
			return;
		}
		for(Iterator<tmpBlock> it = bMap.downMap.iterator(); it.hasNext();) {
			
			tmpBlock tmpBlock = it.next();
			
			int index = bMap.downMap.indexOf(tmpBlock);
			
			BlockPosition start = tmpBlock.isInside(range);
			BlockPosition end = tmpBlock.isInside(range + size);

			if (start == BlockPosition.BLOCK_POS_INSIDE) {
				// 终点在外部
				// n  ___
				// s ———
				if (end != BlockPosition.BLOCK_POS_INSIDE) {
					it.remove();
					tmpBlock.size = range + size - tmpBlock.range;
					bMap.downMap.add(index, tmpBlock);
					break;
				}
				
			} else if(start == BlockPosition.BLOCK_POS_LEFT) {
				// n __
				// s    ——
				if(end == BlockPosition.BLOCK_POS_LEFT) {
					if (index > 0) {
						tmpBlock newBlock = new tmpBlock();
						newBlock.range = range;
						newBlock.size = size;
						bMap.downMap.add(index - 1, newBlock);
					}
					break;
				// n ___
				// s  ———
				} else if(end == BlockPosition.BLOCK_POS_INSIDE) {
					it.remove();
					tmpBlock.size = tmpBlock.range + tmpBlock.size - range;
					tmpBlock.range = range;
					bMap.downMap.add(index, tmpBlock);
					break;
				// n _____
				// s  ———
				} else {
					it.remove();
					break;
				}
			} else if(!it.hasNext()) {
				tmpBlock newBlock = new tmpBlock();
				newBlock.range = range;
				newBlock.size = size;
				bMap.downMap.add(newBlock);
				break;
			}
		}
		try {
			persistCfgFile(gson.toJson(bMap));
		} catch (IOException e) {
			Log.e(TAG, "Failed to persist Mapping.");
			e.printStackTrace();
		}
	}
	
	private void persistCfgFile(String json) throws IOException {
		if (!cfgfile.exists()) {
			cfgfile.createNewFile();
		}
		FileWriter fw = new FileWriter(cfgfile);
		String s = json;
		fw.write(s, 0, s.length());
		fw.flush();
		fw.close();
	}
	
	private String readCfgFile() throws IOException {
		String ret = "";
		if (cfgfile.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(cfgfile));
			String data = br.readLine();// 一次读入一行，直到读入null为文件结束
			while (data != null) {
				System.out.println(data);
				ret += data;
				data = br.readLine(); // 接着读下一行
			}
			br.close();
		}
		return ret;
	}
	
	static class FileWriteThread extends Thread {
		private long skip;  //从哪个位置开始
		private byte[] content; //写的内容
		private String path; 
		public FileWriteThread(int offset , byte[] content , String path){
			this.skip = offset;
			this.content = content;
			this.path = path;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			RandomAccessFile raf  = null;
			try {
				raf = new RandomAccessFile(path,"rw");
				raf.seek(skip);
				raf.write(content);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					if(raf != null){
						raf.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
