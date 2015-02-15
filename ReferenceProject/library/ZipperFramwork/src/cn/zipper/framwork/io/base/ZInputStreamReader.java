package cn.zipper.framwork.io.base;

import org.apache.http.util.ByteArrayBuffer;

import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public abstract class ZInputStreamReader {
	
	private ZPercent percent;
	private boolean isEnding;
	private boolean isBreak;
	
	
	public ZInputStreamReader(OnPercentChangedListener listener) {
		this.percent = new ZPercent(listener);
	}
	
	protected abstract void createInputStream();
	public abstract long getAvailable();
	protected abstract int read(byte[] bytes) throws Exception;
	protected abstract void skip(long byteCount) throws Exception;
	public abstract void close();
	
	public final boolean isEnding() {
		return isEnding;
	}
	
	public final boolean isBreak() {
		return isBreak;
	}
	
	public final void stop() {
		isBreak = true;
	}
	
	public final ZPercent getZPercent() {
		return percent;
	}
	
	public final byte[] readByBlockSize(float maxSize, int blockSize) {
		byte[] bytes = null;
		
		try {
			percent.setMaxValue(maxSize);
			
			ByteArrayBuffer buffer = new ByteArrayBuffer(4096);
			int totalLength = 0;
			int readLength = 0;
			byte[] temp = new byte[blockSize];
			while (true) {
				if (isBreak) {
					break;
				}
				readLength = read(temp);
				if (readLength == -1) {
					isEnding = true;
					break;
				} else {
					buffer.append(temp, 0, readLength);
					totalLength += readLength;
					if (readLength < blockSize) {
						byte[] trim = new byte[readLength];
						System.arraycopy(temp, 0, trim, 0, readLength);
						temp = trim;
					}
					percent.setCurrentValueStep(readLength, temp);
				}
			}
			
			bytes = buffer.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	
	//Do not return buf after reading Block, avoid memory leaking
	public final boolean readByBlockSize2(float maxSize, int blockSize) {
		//byte[] bytes = null;
		boolean isOK = true;
		try {
			percent.setMaxValue(maxSize);
			
			//ByteArrayBuffer buffer = new ByteArrayBuffer(4096);
			//int totalLength = 0;
			int readLength = 0;
			byte[] temp = new byte[blockSize];
			while (true) {
				if (isBreak) {
					break;
				}
				readLength = read(temp);
				if (readLength == -1) {
					isEnding = true;
					break;
				} else {
					//buffer.append(temp, 0, readLength);
					//totalLength += readLength;
					if (readLength < blockSize) {
						byte[] trim = new byte[readLength];
						System.arraycopy(temp, 0, trim, 0, readLength);
						temp = trim;
					}
					percent.setCurrentValueStep(readLength, temp);
				}
			}
			
			//bytes = buffer.toByteArray();
		} catch (Exception e) {
			isOK = false;
			e.printStackTrace();
		}
		return isOK;
		//return bytes;
	}
	
	public final byte[] readAll(float maxSize) {
		byte[] bytes = null;
		
		try {
			percent.setMaxValue(maxSize);
			
			ByteArrayBuffer buffer = new ByteArrayBuffer(4096);
			int readLength = 0;
			byte[] temp = new byte[4096];
			while (true) {
				if (isBreak) {
					break;
				}
				readLength = read(temp);
				if (readLength == -1) {
					isEnding = true;
					break;
				} else {
					buffer.append(temp, 0, readLength);
					percent.setCurrentValueStep(readLength, temp);
				}
			}
			
			bytes = buffer.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bytes;
	}

}
