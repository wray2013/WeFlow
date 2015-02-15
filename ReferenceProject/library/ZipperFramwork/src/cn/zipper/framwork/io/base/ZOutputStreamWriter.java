package cn.zipper.framwork.io.base;

import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public abstract class ZOutputStreamWriter {
	
	private ZPercent percent;
	private boolean isEnding;
	private boolean isBreak;
	
	
	public ZOutputStreamWriter(OnPercentChangedListener listener) {
		this.percent = new ZPercent(listener);
	}
	
	protected abstract void createOutputStream();
	protected abstract void write(byte[] bytes, int offset, int length) throws Exception;
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
	
	public final boolean writeByBlockSize(byte[] bytes, int blockSize) {
		boolean b = false;
		try {
//			if (bytes != null) {
				percent.setMaxValue(bytes.length);
				
				int offset = 0;
				int length = 0;
				
				while (true) {
					if (isBreak) {
						break;
					}
					if (offset + blockSize < bytes.length) {
						length = blockSize;
					} else {
						length = bytes.length - offset;
						isEnding = true;
					}
					write(bytes, offset, length);
					percent.setCurrentValueStep(length, bytes);
					offset += length;
					if (isEnding) {
						break;
					}
				}
//			}
			
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return b && !isBreak;
	}
	
	public final boolean writeBlock(float maxSize, byte[] bytesBlock) {
		boolean b = false;
		try {
//			if (bytesBlock != null) {
				percent.setMaxValue(maxSize);
				write(bytesBlock, 0, bytesBlock.length);
				percent.setCurrentValueStep(bytesBlock.length, bytesBlock);
//			}
			
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return b;
	}
	
	public final boolean writeByStep(byte[] bytes, int step) {
		boolean b = false;
		
		try {
//			if (bytes != null) {
				int length = bytes.length;
				int lastOffset = 0;
				int blockSize = 0;
				float currentOffset = 0;
				float stepSize = (float)length/step;
				
				percent.setMaxValue(length);
				
				while (true) {
					if (isBreak) {
						break;
					}
					currentOffset += stepSize;
					
					if (currentOffset > length) {
						currentOffset = length;
						isEnding = true;
					}
					if (currentOffset - lastOffset >= 1) {
						blockSize = (int) (currentOffset - lastOffset);
						write(bytes, lastOffset, blockSize);
						lastOffset += blockSize;
					}
					
					percent.setCurrentValue(currentOffset, bytes);
					
					if (isEnding) {
						break;
					}
				}
//			}
			
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b && !isBreak;
	}
	
}
